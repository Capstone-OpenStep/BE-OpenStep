package com.chungang.capstone.openstep.domain.Repo.service;

import com.chungang.capstone.openstep.domain.Repo.dto.RepoResponseDTO;
import com.chungang.capstone.openstep.domain.Repo.dto.GitHubRepoResponse;
import com.chungang.capstone.openstep.domain.Repo.dto.GitHubRepoResponse.Node;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Repo.repository.RepoRepository;
import com.chungang.capstone.openstep.domain.Repo.converter.RepoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepoQueryService {

    private final RepoRepository repoRepository;
    private final GitHubGraphQLService gitHubGraphQLService;

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
                .map(edge -> saveIfNotExists(edge.getNode()))
                .collect(Collectors.toList());


        // DB 저장
        // repoRepository.saveAll(repos);

        return repos;
    }

    private Repo saveIfNotExists(Node node) {
        return repoRepository.findByGithubUrl(node.getUrl())
                .orElseGet(() -> repoRepository.save(toRepoEntity(node)));
    }

    private Repo toRepoEntity(Node node) {
        return Repo.builder()
                .repoName(node.getName())
                .description(node.getDescription())
                .language(node.getPrimaryLanguage() != null ? node.getPrimaryLanguage().getName() : null)
                .stars(node.getStargazerCount())
                .githubUrl(node.getUrl())
                .ownerName(node.getOwner() != null ? node.getOwner().getLogin() : null)
                .build();
    }


    public Repo getRepoById(Long repoId) {
        return repoRepository.findById(repoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레포지토리입니다."));
    }
}
