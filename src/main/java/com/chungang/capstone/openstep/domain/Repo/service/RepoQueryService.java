package com.chungang.capstone.openstep.domain.Repo.service;

import com.chungang.capstone.openstep.domain.common.InterestLanguage;
import com.chungang.capstone.openstep.domain.common.InterestDomain;
import com.chungang.capstone.openstep.domain.Github.service.GitHubGraphQLService;
import com.chungang.capstone.openstep.domain.Github.dto.GitHubRepoResponse;
import com.chungang.capstone.openstep.domain.Github.dto.GitHubRepoResponse.Node;
import com.chungang.capstone.openstep.domain.Github.util.GitHubQueryBuilder;
import com.chungang.capstone.openstep.domain.Member.repository.MemberDomainRepository;
import com.chungang.capstone.openstep.domain.Member.repository.MemberLanguageRepository;
import com.chungang.capstone.openstep.domain.OpenAI.service.OpenAIService;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Repo.repository.RepoRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.GithubGraphQLException;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.RepoHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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


    public List<Repo> getTrendingRepos() {
        GitHubRepoResponse gitHubRepoResponse = gitHubGraphQLService.fetchTrendingRepositories();

        if (gitHubRepoResponse == null ||
                gitHubRepoResponse.getData() == null ||
                gitHubRepoResponse.getData().getSearch() == null ||
                gitHubRepoResponse.getData().getSearch().getEdges() == null) {
            throw new IllegalStateException("GitHub 응답 파싱 실패: null 필드 존재");
        }

        List<Repo> repos = gitHubRepoResponse.getData().getSearch().getEdges()
                .stream()
                .map(edge -> edge.getNode())
                //.filter(node -> node.getStargazerCount() > 10000) // 별점 1,000 이상인 레포지토리만 필터링
                .filter(node -> node.getOpenIssuesCount() > 0 && node.getGoodFirstIssueCount() > 0) // 오픈 이슈가 1개 이상이고, goodFirstIssue가 1개 이상인 레포지토리만 필터링
                .map(this::saveIfNotExists)
                .collect(Collectors.toList());

        return repos;
    }

    private Repo saveIfNotExists(Node node) {
        return repoRepository.findByGithubUrl(node.getUrl())
                .orElseGet(() -> {
                    try {
                        return repoRepository.save(toRepoEntity(node));
                    } catch (Exception e) {
                        // 이미 저장되어 있는 경우 (중복 insert 방지)
                        log.warn("이미 저장된 Repo입니다. URL: {}", node.getUrl());
                        return repoRepository.findByGithubUrl(node.getUrl())
                                .orElseThrow(() -> new IllegalStateException("중복 삽입 후 조회 실패"));
                    }
                });
    }


    private Repo toRepoEntity(Node node) {
        String description = node.getDescription() != null ? node.getDescription() : "";
        String readme = "README.md";
        String summary = openAIService.summarizeRepo(description, readme);
        String refinedSummary = openAIService.rewriteNaturalKorean(summary);

        return Repo.builder()
                .repoName(node.getName())
                .description(node.getDescription())
                .summary(refinedSummary)
                .language(node.getPrimaryLanguage() != null ? node.getPrimaryLanguage().getName() : null)
                .stars(node.getStargazerCount())
                .githubUrl(node.getUrl())
                .ownerName(node.getOwner() != null ? node.getOwner().getLogin() : null)
                .forks(node.getForkCount() != null ? node.getForkCount() : 0)
                .openIssues(node.getOpenIssues() != null ? node.getOpenIssues().getTotalCount() : 0)
                .closedIssues(node.getClosedIssues() != null ? node.getClosedIssues().getTotalCount() : 0)
                .watchers(node.getWatchers() != null ? node.getWatchers().getTotalCount() : 0)
                .watchers(node.getWatchersCount())
                .lastGithubUpdate(node.getUpdatedAt() != null ? OffsetDateTime.parse(node.getUpdatedAt()).toLocalDateTime() : null)
                .build();
    }


    public Repo getRepoById(Long repoId) {
        return repoRepository.findById(repoId)
                .orElseThrow(() -> new RepoHandler(ErrorStatus.REPO_NOT_FOUND));
    }


    // 레포지토리 이름으로 검색
    public List<Repo> getReposByName(Optional<String> keyword) {
        String searchKeyword = keyword.orElse("").trim();

        if (searchKeyword.isEmpty()) { return repoRepository.findTop10ByOrderByStarsDesc();}
        return repoRepository.findTop10ByRepoNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByStarsDesc(
                searchKeyword, searchKeyword
        );
    }


    // 사용자 맞춤 레포지토리 추천
//    public List<Repo> getSuggestedRepos(Long memberId) {
//        // 1. 관심사 조회
//        List<String> languages = memberLanguageRepository.findLanguagesByMemberId(memberId)
//                .stream()
//                .map(InterestLanguage::getLabel)
//                .collect(Collectors.toList());
//
//        List<String> domains = memberDomainRepository.findDomainsByMemberId(memberId)
//                .stream()
//                .map(InterestDomain::getLabel)
//                .collect(Collectors.toList());
//
//        if (languages.isEmpty() && domains.isEmpty()) {
//            throw new RepoHandler(ErrorStatus.REPO_NO_INTEREST_INFO);
//        }
//
//        // 2. GitHub 검색 쿼리 생성
//        String query = GitHubQueryBuilder.buildSearchQuery(languages, domains);
//
//        // 3. GraphQL 호출
//        GitHubRepoResponse response = gitHubGraphQLService.searchRepositories(query);
//        if (response == null || response.getData() == null || response.getData().getSearch() == null) {
//            throw new GithubGraphQLException(ErrorStatus.GITHUB_GRAPHQL_ERROR);
//        }
//
//        // 4. 필터링 및 저장
//        List<Repo> filteredRepos = response.getData().getSearch().getEdges().stream()
//                .map(edge -> edge.getNode())
//                .filter(node ->
//                        // 사용자의 관심 언어 또는 도메인과 연관된 레포 + 최소 star 수 조건
//                        (node.getPrimaryLanguage() != null && languages.contains(node.getPrimaryLanguage().getName())) ||
//                                (node.getDescription() != null && domains.stream().anyMatch(domain -> node.getDescription().toLowerCase().contains(domain.toLowerCase())))
//                )
//                .filter(node -> node.getStargazerCount() >= 50) // 최소 star 필터
//                .sorted(Comparator.comparing(Node::getStargazerCount).reversed()) // 인기순 정렬
//                .limit(10)
//                .map(this::saveIfNotExists)
//                .collect(Collectors.toList());
//
//        log.info("languages: {}", languages);
//        log.info("domains: {}", domains);
//
//        return filteredRepos;
//    }

//    public List<Repo> getSuggestedRepos(Long memberId) {
//        // 1. 관심사 조회
//        List<String> languages = memberLanguageRepository.findLanguagesByMemberId(memberId)
//                .stream().map(InterestLanguage::getLabel).toList();
//        List<String> domains = memberDomainRepository.findDomainsByMemberId(memberId)
//                .stream().map(InterestDomain::getLabel).toList();
//
//        if (languages.isEmpty() && domains.isEmpty()) {
//            throw new RepoHandler(ErrorStatus.REPO_NO_INTEREST_INFO);
//        }
//
//        log.info("languages: {}", languages);
//        log.info("domains: {}", domains);
//
//        // 2. OR 스타일 쿼리 빌드
//        String query = GitHubQueryBuilder.buildExplicitQuery(languages, domains);
//        log.info("GraphQL query: {}", query);
//
//        // 3. GitHub GraphQL 호출
//        GitHubRepoResponse response = gitHubGraphQLService.searchRepositories(query);
//        if (response == null || response.getData() == null || response.getData().getSearch() == null) {
//            throw new GithubGraphQLException(ErrorStatus.GITHUB_GRAPHQL_ERROR);
//        }
//
//        // 4. 결과 필터링 및 정렬
//        return response.getData().getSearch().getEdges().stream()
//                .map(edge -> edge.getNode())
//                .filter(node -> node.getStargazerCount() > 50) // 최소 스타 수 조건
//                .sorted(Comparator.comparing(Node::getStargazerCount).reversed())
//                .limit(10)
//                .map(this::saveIfNotExists)
//                .toList();
//    }

    public List<Repo> getSuggestedRepos(Long memberId) {
        List<String> languages = memberLanguageRepository.findLanguagesByMemberId(memberId)
                .stream()
                .map(InterestLanguage::getLabel)
                .toList();
        List<String> domains = memberDomainRepository.findDomainsByMemberId(memberId)
                .stream()
                .map(InterestDomain::getLabel)
                .toList();

        if (languages.isEmpty() && domains.isEmpty()) {
            throw new RepoHandler(ErrorStatus.REPO_NO_INTEREST_INFO);
        }

        Set<Repo> resultSet = new HashSet<>();

        // 관심 언어 기반 추천 최대 5개
        for (String lang : languages) {
            String query = GitHubQueryBuilder.buildSearchQuery(List.of(lang), List.of());
            GitHubRepoResponse response = gitHubGraphQLService.searchRepositories(query);
            resultSet.addAll(
                    parseResponseAndSave(response).stream()
                            .sorted(Comparator.comparing(Repo::getStars).reversed())
                            .limit(5)
                            .toList()
            );
            if (resultSet.size() >= 10) break;
        }

        // 관심 도메인 기반 추천 최대 5개
        for (String domain : domains) {
            String query = GitHubQueryBuilder.buildSearchQuery(List.of(), List.of(domain));
            GitHubRepoResponse response = gitHubGraphQLService.searchRepositories(query);
            resultSet.addAll(
                    parseResponseAndSave(response).stream()
                            .sorted(Comparator.comparing(Repo::getStars).reversed())
                            .limit(5)
                            .toList()
            );
            if (resultSet.size() >= 10) break;
        }

        // fallback: 트렌딩
        if (resultSet.isEmpty()) {
            resultSet.addAll(getTrendingRepos());
        }

        return resultSet.stream()
                .sorted(Comparator.comparing(Repo::getStars).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }


    private List<Repo> parseResponseAndSave(GitHubRepoResponse response) {
        if (response == null || response.getData() == null || response.getData().getSearch() == null) {
            return List.of();
        }

        return response.getData().getSearch().getEdges().stream()
                .map(edge -> edge.getNode())
                .filter(node -> node.getStargazerCount() > 50)
                .map(this::saveIfNotExists)
                .collect(Collectors.toList());
    }





}
