package com.chungang.capstone.openstep.domain.Repo.service;

import com.chungang.capstone.openstep.domain.Github.dto.GitHubRepoResponse;
import com.chungang.capstone.openstep.domain.Github.dto.GitHubRepoResponse.Node;
import com.chungang.capstone.openstep.domain.Github.service.GitHubGraphQLService;
import com.chungang.capstone.openstep.domain.Github.util.GitHubQueryBuilder;
import com.chungang.capstone.openstep.domain.Member.repository.MemberDomainRepository;
import com.chungang.capstone.openstep.domain.Member.repository.MemberLanguageRepository;
import com.chungang.capstone.openstep.domain.OpenAI.service.OpenAIService;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Repo.repository.RepoRepository;
import com.chungang.capstone.openstep.domain.common.InterestDomain;
import com.chungang.capstone.openstep.domain.common.InterestLanguage;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.RepoHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepoQueryService {

    private final RepoRepository repoRepository;
    private final MemberLanguageRepository memberLanguageRepository;
    private final MemberDomainRepository memberDomainRepository;
    private final GitHubGraphQLService gitHubGraphQLService;
    private final OpenAIService openAIService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RepoCacheService repoCacheService;

    public List<Repo> getTrendingRepos() {
        GitHubRepoResponse gitHubRepoResponse = gitHubGraphQLService.fetchTrendingRepositories();

        if (gitHubRepoResponse == null ||
                gitHubRepoResponse.getData() == null ||
                gitHubRepoResponse.getData().getSearch() == null ||
                gitHubRepoResponse.getData().getSearch().getEdges() == null) {
            throw new IllegalStateException("GitHub 응답 파싱 실패: null 필드 존재");
        }

        return gitHubRepoResponse.getData().getSearch().getEdges()
                .stream()
                .map(edge -> edge.getNode())
                .filter(node -> node.getOpenIssuesCount() > 0 && node.getBeginnerIssueCount() > 0)
                .map(this::saveIfNotExists)
                .collect(Collectors.toList());
    }

    private Repo saveIfNotExists(Node node) {
        String url = node.getUrl();
        Optional<Repo> existing = repoRepository.findByGithubUrl(url);
        if (existing.isPresent()) return existing.get();

        Repo saved = repoRepository.save(toRepoEntity(node));
        return repoRepository.findByGithubUrl(url).orElseThrow();
    }

    private Repo toRepoEntity(Node node) {
        String description = node.getDescription() != null ? node.getDescription() : "";
        //String summary = openAIService.rewriteNaturalKorean(openAIService.summarizeRepo(description, "README.md"));
        String summary = "Not used";
        return Repo.builder()
                .repoName(node.getName())
                .description(node.getDescription())
                .summary("Not used")
                .language(node.getPrimaryLanguage() != null ? node.getPrimaryLanguage().getName() : null)
                .stars(node.getStargazerCount())
                .githubUrl(node.getUrl())
                .ownerName(node.getOwner() != null ? node.getOwner().getLogin() : null)
                .forks(node.getForkCount() != null ? node.getForkCount() : 0)
                .openIssues(node.getOpenIssues() != null ? node.getOpenIssues().getTotalCount() : 0)
                .closedIssues(node.getClosedIssues() != null ? node.getClosedIssues().getTotalCount() : 0)
                .watchers(node.getWatchers() != null ? node.getWatchers().getTotalCount() : 0)
                .lastGithubUpdate(node.getUpdatedAt() != null ? OffsetDateTime.parse(node.getUpdatedAt()).toLocalDateTime() : null)
                .beginnerIssueCount(node.getBeginnerIssueCount())
                .build();
    }

    public Repo getRepoById(Long repoId) {
        return repoRepository.findById(repoId).orElseThrow(() -> new RepoHandler(ErrorStatus.REPO_NOT_FOUND));
    }

    public List<Repo> getReposByName(Optional<String> keyword) {
        String searchKeyword = keyword.orElse("").trim();
        return searchKeyword.isEmpty()
                ? repoRepository.findTop10ByOrderByStarsDesc()
                : repoRepository.findTop10ByRepoNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByStarsDesc(searchKeyword, searchKeyword);
    }

    public List<Repo> getSuggestedRepos(Long memberId) {
        List<String> languages = memberLanguageRepository.findLanguagesByMemberId(memberId)
                .stream().map(InterestLanguage::getLabel).toList();
        List<String> domains = memberDomainRepository.findDomainsByMemberId(memberId)
                .stream().map(InterestDomain::getLabel).toList();

        if (languages.isEmpty() && domains.isEmpty()) {
            throw new RepoHandler(ErrorStatus.REPO_NO_INTEREST_INFO);
        }

        Map<String, Repo> repoMap = new HashMap<>();

        for (String lang : languages) {
            for (String domain : domains) {
                List<Repo> cached = repoCacheService.getReposByLanguageAndDomain(lang, domain);
                if (cached != null) {
                    log.info("Cache Hit: {} + {}", lang, domain);
                    cached.forEach(repo -> repoMap.putIfAbsent(repo.getGithubUrl(), repo));
                    continue;
                }

                String query = GitHubQueryBuilder.buildSearchQuery(List.of(lang), List.of(domain));
                GitHubRepoResponse response = gitHubGraphQLService.searchRepositories(query);
                List<Repo> parsed = parseResponseAndSave(response).stream().limit(10).toList();

                repoCacheService.saveReposByLanguageAndDomain(lang, domain, parsed);
                parsed.forEach(repo -> repoMap.putIfAbsent(repo.getGithubUrl(), repo));
            }
        }

        if (repoMap.isEmpty()) {
            String looseQuery = GitHubQueryBuilder.buildLooseSearchQuery(languages, domains);
            GitHubRepoResponse looseResponse = gitHubGraphQLService.searchRepositories(looseQuery);
            List<Repo> looseParsed = parseResponseAndSave(looseResponse).stream().limit(10).toList();
            looseParsed.forEach(repo -> repoMap.putIfAbsent(repo.getGithubUrl(), repo));
        }

        List<Repo> sorted = repoMap.values().stream()
                .sorted(Comparator.comparingInt(r -> -1 * (r.getStars() + r.getBeginnerIssueCount())))
                .limit(20)
                .toList();

        repoCacheService.saveRecommendedRepos(memberId, sorted);
        return sorted;
    }

    private List<Repo> parseResponseAndSave(GitHubRepoResponse response) {
        if (response == null || response.getData() == null || response.getData().getSearch() == null) {
            log.warn("[!] GitHub 응답이 비어있음");
            return List.of();
        }

        List<Node> allNodes = response.getData().getSearch().getEdges().stream().map(GitHubRepoResponse.Edge::getNode).toList();

        log.info("[*] 총 {}개의 레포지토리 검색 결과", allNodes.size());
        for (Node node : allNodes) {
            log.info(" {} |  {} | issues: {} | beginner: {} | updated: {}",
                    node.getName(),
                    node.getStargazerCount(),
                    node.getOpenIssuesCount(),
                    node.getBeginnerIssueCount(),
                    node.getUpdatedAt()
            );
        }

        List<Repo> strictFiltered = allNodes.stream()
                .filter(node -> node.getBeginnerIssueCount() >= 1)
                .filter(node -> node.getOpenIssuesCount() > 0)
                //.filter(node -> node.getStargazerCount() < 100000)
                .filter(node -> isUpdatedWithin36Months(node.getUpdatedAt()))
                .map(this::saveIfNotExists)
                .collect(Collectors.toList());

        if (strictFiltered.size() >= 5) return strictFiltered;

        return allNodes.stream()
                .filter(node -> node.getBeginnerIssueCount() >= 0)
                .filter(node -> node.getOpenIssuesCount() > 0)
                .filter(node -> isUpdatedWithin36Months(node.getUpdatedAt()))
                .map(this::saveIfNotExists)
                .limit(10)
                .collect(Collectors.toList());
    }

    private boolean isUpdatedWithin36Months(String isoDate) {
        try {
            return ZonedDateTime.parse(isoDate).isAfter(ZonedDateTime.now().minusMonths(36));
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // 언어, 도메인 분리하여 조회
    public List<Repo> getSuggestedReposBySplitQuery(Long memberId) {
        List<String> languages = memberLanguageRepository.findLanguagesByMemberId(memberId)
                .stream().map(InterestLanguage::getLabel).toList();
        List<String> domains = memberDomainRepository.findDomainsByMemberId(memberId)
                .stream().map(InterestDomain::getLabel).toList();

        if (languages.isEmpty() && domains.isEmpty()) {
            throw new RepoHandler(ErrorStatus.REPO_NO_INTEREST_INFO);
        }

        Map<String, Repo> repoMap = new HashMap<>();

        // 언어 기반 쿼리
        for (String lang : languages) {
            List<Repo> cached = repoCacheService.getReposByLanguageAndDomain(lang, "");
            if (cached != null) {
                log.info("Cache Hit: {}", lang);
                cached.forEach(repo -> repoMap.putIfAbsent(repo.getGithubUrl(), repo));
                continue;
            }

            String query = GitHubQueryBuilder.buildSearchQuery(List.of(lang), List.of());
            GitHubRepoResponse response = gitHubGraphQLService.searchRepositories(query);
            List<Repo> parsed = parseResponseAndSave(response).stream().limit(10).toList();

            repoCacheService.saveReposByLanguageAndDomain(lang, "", parsed);
            parsed.forEach(repo -> repoMap.putIfAbsent(repo.getGithubUrl(), repo));
        }

        // 도메인 기반 쿼리
        for (String domain : domains) {
            List<Repo> cached = repoCacheService.getReposByLanguageAndDomain("", domain);
            if (cached != null) {
                log.info("Cache Hit: {}", domain);
                cached.forEach(repo -> repoMap.putIfAbsent(repo.getGithubUrl(), repo));
                continue;
            }

            String query = GitHubQueryBuilder.buildSearchQuery(List.of(), List.of(domain));
            GitHubRepoResponse response = gitHubGraphQLService.searchRepositories(query);
            List<Repo> parsed = parseResponseAndSave(response).stream().limit(10).toList();

            repoCacheService.saveReposByLanguageAndDomain("", domain, parsed);
            parsed.forEach(repo -> repoMap.putIfAbsent(repo.getGithubUrl(), repo));
        }

        if (repoMap.isEmpty()) {
            getTrendingRepos().forEach(repo -> repoMap.putIfAbsent(repo.getGithubUrl(), repo));
        }

        List<Repo> sorted = repoMap.values().stream()
                .sorted(Comparator.comparingInt(r -> -1 * (r.getStars() + r.getBeginnerIssueCount())))
                .limit(20)
                .toList();

        repoCacheService.saveRecommendedRepos(memberId, sorted);
        return sorted;
    }
}
