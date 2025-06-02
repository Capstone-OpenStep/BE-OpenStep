package com.chungang.capstone.openstep.domain.Issue.service;

import com.chungang.capstone.openstep.domain.Bookmark.repository.BookmarkRepository;
import com.chungang.capstone.openstep.domain.Github.dto.GitHubIssueResponse;
import com.chungang.capstone.openstep.domain.Github.service.GitHubGraphQLService;
import com.chungang.capstone.openstep.domain.Issue.converter.IssueConverter;
import com.chungang.capstone.openstep.domain.Issue.dto.IssueResponseDTO;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Issue.repository.IssueRepository;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.repository.MemberDomainRepository;
import com.chungang.capstone.openstep.domain.Member.repository.MemberLanguageRepository;
import com.chungang.capstone.openstep.domain.OpenAI.service.OpenAIService;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Repo.repository.RepoRepository;
import com.chungang.capstone.openstep.domain.Repo.service.RepoQueryService;
import com.chungang.capstone.openstep.domain.common.InterestDomain;
import com.chungang.capstone.openstep.domain.common.InterestLanguage;
import com.chungang.capstone.openstep.domain.common.UpdatePeriod;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.IssueHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class IssueQueryService {
    private final RepoRepository repoRepository;
    private final IssueRepository issueRepository;
    private final RepoQueryService repoQueryService;
    private final GitHubGraphQLService gitHubGraphQLService;
    private final IssueCacheService issueCacheService;
    private final OpenAIService openAIService;
    private final MemberLanguageRepository memberLanguageRepository;
    private final MemberDomainRepository memberDomainRepository;
    private final BookmarkRepository bookmarkRepository;

//    public List<Issue> getTrendingIssues(Pageable pageable) {
//        List<Repo> repos = repoRepository.findAll();
//        List<Issue> allIssues = new ArrayList<>();
//        for (Repo repo : repoRepository.findTop10ByOrderByStarsDesc()) {
//            GitHubIssueResponse res = gitHubGraphQLService.fetchIssuesByRepo(repo.getOwnerName(), repo.getRepoName());
//            if (res != null && res.getData().getRepository() != null) {
//                List<Issue> issues = res.getData().getRepository().getIssues().getNodes().stream()
//                        .map(node -> {
//                            Optional<Issue> existing = issueRepository.findByGithubUrl(node.getUrl());
//                            if (existing.isPresent()) {
//                                Issue issue = existing.get();
//                                LocalDateTime updated = OffsetDateTime.parse(node.getUpdatedAt()).toLocalDateTime();
//                                if (issue.getUpdatedAt().isEqual(updated)) return issue; // skip
//                            }
//                            return saveIfNotExistsOrUpdate(node, repo);
//                        })
//                        .filter(Objects::nonNull)
//                        .toList();
//                allIssues.addAll(issues);
//            }
//            if (allIssues.size() >= 20) break;
//        }
//        List<Issue> top20 = allIssues.stream()
//                .sorted(Comparator.comparing(Issue::getUpdatedAt).reversed())
//                .limit(20)
//                .toList();
//
//        int start = (int) pageable.getOffset();
//        int end = Math.min(start + pageable.getPageSize(), top20.size());
//        if (start >= end) return List.of();
//        return top20.subList(start, end);
//    }

    public List<Issue> getTrendingIssues(Pageable pageable) {
        List<Issue> cached = issueCacheService.getTrendingIssuesFromCache();
        if (cached != null && !cached.isEmpty()) {
            log.info("[TRENDING] Cache hit: {} issues", cached.size());
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), cached.size());
            return (start >= end) ? List.of() : cached.subList(start, end);
        }

        log.info("[TRENDING] Cache miss: fetching from GitHub...");
        List<Issue> allIssues = new ArrayList<>();

        for (Repo repo : repoRepository.findTop10ByOrderByStarsDesc()) {
            GitHubIssueResponse res = gitHubGraphQLService.fetchIssuesByRepo(repo.getOwnerName(), repo.getRepoName());
            if (res != null && res.getData().getRepository() != null) {
                List<Issue> issues = res.getData().getRepository().getIssues().getNodes().stream()
                        .map(node -> {
                            Optional<Issue> existing = issueRepository.findByGithubUrl(node.getUrl());
                            if (existing.isPresent()) {
                                Issue issue = existing.get();
                                LocalDateTime updated = OffsetDateTime.parse(node.getUpdatedAt()).toLocalDateTime();
                                if (issue.getUpdatedAt().isEqual(updated)) return issue;
                            }
                            return saveIfNotExistsOrUpdate(node, repo);
                        })
                        .filter(Objects::nonNull)
                        .toList();
                allIssues.addAll(issues);
            }
        }

        // 정렬 및 캐시 저장
        List<Issue> sorted = allIssues.stream()
                .sorted(Comparator.comparing(Issue::getUpdatedAt).reversed())
                .toList();

        issueCacheService.saveTrendingIssues(sorted);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sorted.size());
        return (start >= end) ? List.of() : sorted.subList(start, end);
    }



    public List<Issue> getSuggestedIssues(Member member, Pageable pageable) {
        Long memberId = member.getMemberId();
        log.info("[ISSUE_RECOMMEND] Start for memberId = {}", memberId);

        //issueCacheService.evict(memberId);
        // 현재 관심사 기반 해시 계산
        String currentHash = computeInterestHash(memberId);
        String cachedHash = issueCacheService.getInterestHash(memberId);
        List<Issue> cached = issueCacheService.getRecommendedIssues(memberId);
        if (cached != null && currentHash.equals(cachedHash)) {
            log.info("[ISSUE_RECOMMEND] Cache hit: {} issues (interests same)", cached.size());
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), cached.size());
            if (start >= end) return List.of();
            return cached.subList(start, end);
        }

        // 관심사 바뀐 경우 캐시 무효화
        log.info("[ISSUE_RECOMMEND] Interests changed or no cache, regenerating...");
        issueCacheService.evict(memberId);
        issueCacheService.evictInterestHash(memberId);

        //List<Repo> suggestedRepos = repoQueryService.getSuggestedReposBySplitQuery(memberId);
        List<Repo> suggestedRepos = repoQueryService.getSuggestedRepos(memberId);
        log.info("[ISSUE_RECOMMEND] Suggested repos count = {}", suggestedRepos.size());

        List<Issue> collectedIssues = new ArrayList<>();
        List<Issue> fallbackIssues = new ArrayList<>();
        OffsetDateTime threeMonthsAgo = OffsetDateTime.now().minusMonths(9);

        for (Repo repo : suggestedRepos) {
            String owner = repo.getOwnerName();
            String name = repo.getRepoName();
            log.info("[ISSUE_RECOMMEND] Fetching issues from repo: {}/{}", owner, name);

            GitHubIssueResponse response = gitHubGraphQLService.fetchIssuesByRepo(owner, name);
            if (response == null || response.getData() == null || response.getData().getRepository() == null) {
                log.warn("[ISSUE_RECOMMEND] Null or incomplete response from repo: {}/{}", owner, name);
                continue;
            }

            List<GitHubIssueResponse.IssueNode> nodes = response.getData().getRepository().getIssues().getNodes();

            int repoCollected = 0;
            int repoFallback = 0;

            for (GitHubIssueResponse.IssueNode node : nodes) {
                OffsetDateTime updatedAt = OffsetDateTime.parse(node.getUpdatedAt());
                if (updatedAt.isBefore(threeMonthsAgo)) continue;

                //if (node.getState() == null || !node.getState().equalsIgnoreCase("OPEN")) continue;

                String body = Optional.ofNullable(node.getBody()).orElse("").trim();
                if (body.isBlank() || body.equalsIgnoreCase("No description provided")) continue;

                // 중복 방지: githubUrl 기준 확인
                if (issueRepository.findByGithubUrl(node.getUrl()).isPresent()) continue;

                Issue issue = Issue.builder()
                        .repo(repo)
                        .title(node.getTitle())
                        .body(Optional.ofNullable(node.getBody()).orElse("내용 없음"))
                        .summary(null)
                        .githubUrl(node.getUrl())
                        .author(node.getAuthor() != null ? node.getAuthor().getLogin() : "Unknown")
                        .authorAvatarUrl(node.getAuthor() != null ? node.getAuthor().getAvatarUrl() : null)
                        .createdAt(OffsetDateTime.parse(node.getCreatedAt()).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime())
                        .updatedAt(updatedAt.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime())
                        .language(repo.getLanguage())
                        .labels(node.getLabels().getNodes().stream().map(GitHubIssueResponse.LabelNode::getName).toList())
                        .state("OPEN")
                        .build();

                boolean isBeginnerLabel = issue.getLabels().stream()
                        .map(String::toLowerCase)
                        .anyMatch(label ->
                                label.contains("good") ||
                                        label.contains("help") ||
                                        label.contains("beginner") ||
                                        label.contains("starter") ||
                                        label.contains("easy") ||
                                        label.contains("document") ||
                                        label.contains("first")
                        );
                if (isBeginnerLabel) {
                    collectedIssues.add(issue);
                    repoCollected++;
                } else {
                    fallbackIssues.add(issue);
                    repoFallback++;
                }
                if (collectedIssues.size() >= 50) break;
            }
            log.info("[ISSUE_RECOMMEND] Repo: {} -> Collected: {}, Fallback: {}", name, repoCollected, repoFallback);
            if (collectedIssues.size() >= 50) break;
        }

        // 상위 추천 20개로 구성
        int need = 20;
        List<Issue> top20 = collectedIssues.stream()
                .sorted(Comparator.comparing(Issue::getUpdatedAt).reversed())
                .limit(need)
                .collect(Collectors.toList());

        if (top20.size() < need) {
            int remaining = need - top20.size();
            List<Issue> fallback = fallbackIssues.stream()
                    .sorted(Comparator.comparing(Issue::getUpdatedAt).reversed())
                    .limit(remaining)
                    .toList();
            top20.addAll(fallback);
        }

        // 요약 + 저장 (중복 방지 포함)
        log.info("[ISSUE_RECOMMEND] Recommended Issues:");
        List<Issue> summarized = top20.stream()
                .map(issue -> {
                    String summary;
                    try {
                        String raw = openAIService.summarizeIssue(issue.getTitle(), issue.getBody());
                        summary = openAIService.rewriteNaturalKorean(raw);
                    } catch (Exception e) {
                        log.warn("[SUMMARY] OpenAI 요약 실패: {}", issue.getGithubUrl(), e);
                        summary = "요약을 생성할 수 없습니다.";
                    }
                    issue.setSummary(summary);
                    log.info(" - [{}] {} ({})", issue.getIssueId(), issue.getTitle(), issue.getGithubUrl());

                    // 중복 저장 방지
                    return issueRepository.findByGithubUrl(issue.getGithubUrl())
                            .orElseGet(() -> issueRepository.save(issue));
                })
                .toList();

        // 캐시 저장
        issueCacheService.saveRecommendedIssues(memberId, summarized);
        issueCacheService.saveInterestHash(memberId, currentHash);
        log.info("[ISSUE_RECOMMEND] Final recommended issues count = {}", summarized.size());
        if (summarized.size() < 20) {
            log.warn("[ISSUE_RECOMMEND] WARNING: Recommended issue count below target ({} / 20)", summarized.size());
        }
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), summarized.size());
        if (start >= end) return List.of();
        return summarized.subList(start, end);
    }


    private Issue saveIfNotExistsOrUpdate(GitHubIssueResponse.IssueNode node, Repo repo) {
        Optional<Issue> existingOpt = issueRepository.findByGithubUrl(node.getUrl());
        String body = Optional.ofNullable(node.getBody()).orElse("내용 없음");
        LocalDateTime githubUpdatedAt = OffsetDateTime.parse(node.getUpdatedAt()).toLocalDateTime();

        if (existingOpt.isPresent()) {
            Issue existing = existingOpt.get();

            // updatedAt이 동일하면 저장하지 않음
            if (existing.getUpdatedAt() != null && existing.getUpdatedAt().isEqual(githubUpdatedAt)) {
                return existing;
            }

            existing.setTitle(node.getTitle());
            existing.setBody(body);
            existing.setUpdatedAt(githubUpdatedAt);
            existing.setSummary(updateSummary(existing));
            return issueRepository.save(existing);
        }
        Issue issue = toIssueEntity(node, repo, body);
        return issueRepository.save(issue);
    }


    private Issue toIssueEntity(GitHubIssueResponse.IssueNode node, Repo repo, String body) {
        String summary = updateSummary(Issue.builder()
                .title(node.getTitle())
                .body(body)
                .build());

        return Issue.builder()
                .repo(repo)
                .title(node.getTitle())
                .body(body)
                .summary(summary)
                .githubUrl(node.getUrl())
                .author(node.getAuthor() != null ? node.getAuthor().getLogin() : "unknown")
                .authorAvatarUrl(node.getAuthor() != null ? node.getAuthor().getAvatarUrl() : null)
                .labels(new ArrayList<>(
                        node.getLabels() != null
                                ? node.getLabels().getNodes().stream().map(GitHubIssueResponse.LabelNode::getName).toList()
                                : Collections.emptyList()
                ))
                .createdAt(OffsetDateTime.parse(node.getCreatedAt()).toLocalDateTime())
                .updatedAt(OffsetDateTime.parse(node.getUpdatedAt()).toLocalDateTime())
                .state("OPEN")
                .language(repo.getLanguage())
                .build();
    }

    private String updateSummary(Issue issue) {
        try {
            String raw = openAIService.summarizeIssue(issue.getTitle(), issue.getBody());
            return openAIService.rewriteNaturalKorean(raw);
        } catch (Exception e) {
            log.warn("[SUMMARY] 요약 실패: {}", issue.getGithubUrl(), e);
            return "요약을 생성할 수 없습니다.";
        }
    }



    public Issue getIssueById(Long issueId) {
        return issueRepository.findById(issueId).orElseThrow(() -> new IssueHandler(ErrorStatus.ISSUE_NOT_FOUND));
    }


    public List<Issue> getIssuesByKeyword(Optional<String> optSearch) {
        //Long memberId = member.getMemberId();
        List<Issue> issues = List.of();
        if (optSearch.isPresent() && !optSearch.get().trim().isEmpty()) {
            String search = optSearch.get();
            issues = issueRepository.findAllByTitleContainingIgnoreCaseOrderByCreatedAtDesc(search);
        }
        if (optSearch.isPresent() && !optSearch.get().trim().isEmpty()) {
            String search = optSearch.get();
            issues = issueRepository.findAllBySummaryContainingIgnoreCaseOrderByCreatedAtDesc(search);
        }
        return issues;
    }

    private String computeInterestHash(Long memberId) {
        List<String> languages = memberLanguageRepository.findLanguagesByMemberId(memberId)
                .stream().map(InterestLanguage::getLabel).sorted().toList();
        List<String> domains = memberDomainRepository.findDomainsByMemberId(memberId)
                .stream().map(InterestDomain::getLabel).sorted().toList();

        String combined = String.join(",", languages) + "|" + String.join(",", domains);
        return Integer.toHexString(combined.hashCode());  // 문자열 해시 (간결하게)
    }

    public List<Long> getBookmarkedIssueIds(Long memberId) {
        return bookmarkRepository.findByMember_MemberId(memberId)
                .stream()
                .map(bookmark -> bookmark.getIssue().getIssueId())
                .collect(Collectors.toList());
    }

    public List<Issue> searchGitHubIssuesByKeywordAndFilters(
            String keyword,
            List<InterestLanguage> languages,
            UpdatePeriod updatePeriod,
            Pageable pageable
    ) {
        String languageQuery = (languages != null && !languages.isEmpty())
                ? " language:" + languages.stream()
                .map(InterestLanguage::getLabel)
                .collect(Collectors.joining(" OR "))
                : "";

        String fullQuery = keyword + languageQuery;

        GitHubIssueResponse response = gitHubGraphQLService.searchIssues(fullQuery);
        if (response == null || response.getData() == null) return Collections.emptyList();

        OffsetDateTime threshold = (updatePeriod != null)
                ? updatePeriod.getThresholdTime()
                : OffsetDateTime.MIN;

        List<GitHubIssueResponse.Edge> edges = Optional.ofNullable(response.getData())
                .map(GitHubIssueResponse.Data::getSearch)
                .map(GitHubIssueResponse.Search::getEdges)
                .orElse(Collections.emptyList());

        List<Issue> filtered = edges.stream()
                .map(GitHubIssueResponse.Edge::getNode)
                .filter(Objects::nonNull)
                .filter(node -> node.getUpdatedAt() != null)
                .map(node -> {
                    try {
                        OffsetDateTime updatedAt = OffsetDateTime.parse(node.getUpdatedAt());
                        return updatedAt.isAfter(threshold) ? node : null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(IssueConverter::fromGitHubIssueNode)
                .limit(20)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        if (start >= end) return List.of();

        return filtered.subList(start, end);
    }



}