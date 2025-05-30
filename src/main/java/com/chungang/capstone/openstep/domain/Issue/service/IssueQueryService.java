package com.chungang.capstone.openstep.domain.Issue.service;

import com.chungang.capstone.openstep.domain.Github.dto.GitHubIssueResponse;
import com.chungang.capstone.openstep.domain.Github.service.GitHubGraphQLService;
import com.chungang.capstone.openstep.domain.Github.util.GitHubQueryBuilder;
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
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.IssueHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public List<IssueResponseDTO.IssueSimpleDTO> getTrendingIssues() {
        List<Repo> repos = repoRepository.findAll();
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
                                if (issue.getUpdatedAt().isEqual(updated)) return issue; // skip
                            }
                            return saveIfNotExistsOrUpdate(node, repo);
                        })
                        .filter(Objects::nonNull)
                        .toList();
                allIssues.addAll(issues);
            }
        }

        return IssueConverter.toIssueSimpleDTOs(allIssues);
    }

    public List<Issue> getSuggestedIssues(Member member) {
        Long memberId = member.getMemberId();
        log.info("[ISSUE_RECOMMEND] Start for memberId = {}", memberId);

        //issueCacheService.evict(memberId);
        List<Issue> cached = issueCacheService.getRecommendedIssues(memberId);
        if (cached != null) {
            log.info("[ISSUE_RECOMMEND] Cache hit: {} issues", cached.size());
            return cached;
        }

        List<Repo> suggestedRepos = repoQueryService.getSuggestedRepos(memberId);
        log.info("[ISSUE_RECOMMEND] Suggested repos count = {}", suggestedRepos.size());

        List<Issue> collectedIssues = new ArrayList<>();
        List<Issue> fallbackIssues = new ArrayList<>();
        OffsetDateTime threeMonthsAgo = OffsetDateTime.now().minusMonths(3);

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
                } else {
                    fallbackIssues.add(issue);
                }
                if (collectedIssues.size() >= 50) break;
            }
            if (collectedIssues.size() >= 50) break;
        }

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
                    // 중복 저장 방지
                    return issueRepository.findByGithubUrl(issue.getGithubUrl())
                            .orElseGet(() -> issueRepository.save(issue));
                })
                .toList();

        issueCacheService.saveRecommendedIssues(memberId, summarized);
        log.info("[ISSUE_RECOMMEND] Final recommended issues count = {}", summarized.size());
        return summarized;
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


}