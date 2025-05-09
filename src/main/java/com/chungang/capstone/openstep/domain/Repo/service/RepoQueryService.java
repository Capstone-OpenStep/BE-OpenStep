package com.chungang.capstone.openstep.domain.Repo.service;

import com.chungang.capstone.openstep.domain.Github.service.GitHubGraphQLService;
import com.chungang.capstone.openstep.domain.Github.dto.GitHubRepoResponse;
import com.chungang.capstone.openstep.domain.Github.dto.GitHubRepoResponse.Node;
import com.chungang.capstone.openstep.domain.OpenAI.service.OpenAIService;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Repo.repository.RepoRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.RepoHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepoQueryService {

    private final RepoRepository repoRepository;
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
                .orElseGet(() -> repoRepository.save(toRepoEntity(node)));
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

}
