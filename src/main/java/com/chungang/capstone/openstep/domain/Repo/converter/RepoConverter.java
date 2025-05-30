package com.chungang.capstone.openstep.domain.Repo.converter;

import com.chungang.capstone.openstep.domain.Repo.dto.RepoResponseDTO;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;

import java.util.List;

public class RepoConverter {

    public static List<RepoResponseDTO.TrendingRepoDTO> toTrendingDTOs(List<Repo> repos) {
        return repos.stream()
                .map(repo -> RepoResponseDTO.TrendingRepoDTO.builder()
                        .repoId(repo.getRepoId())
                        .repoName(repo.getRepoName())
                        .summary(repo.getSummary())
                        .ownerName(repo.getOwnerName())
                        .ownerAvatarUrl(repo.getOwnerAvatarUrl())
                        .language(repo.getLanguage())
                        .stars(repo.getStars())
                        .watchers(repo.getWatchers())
                        .forks(repo.getForks())
                        .openIssues(repo.getOpenIssues())
                        .closedIssues(repo.getClosedIssues())
                        .beginnerIssueCount(repo.getBeginnerIssueCount())
                        .githubUrl(repo.getGithubUrl())
                        .description(repo.getDescription())
                        .build())
                .toList();
    }


    public static RepoResponseDTO.RepoDetailDTO toRepoDetailDTO(Repo repo) {
        return RepoResponseDTO.RepoDetailDTO.builder()
                .repoId(repo.getRepoId())
                .repoName(repo.getRepoName())
                .summary(repo.getSummary())
                .ownerName(repo.getOwnerName())
                .ownerAvatarUrl(repo.getOwnerAvatarUrl())
                .description(repo.getDescription())
                .language(repo.getLanguage())
                .stars(repo.getStars())
                .watchers(repo.getWatchers())
                .forks(repo.getForks())
                .openIssues(repo.getOpenIssues())
                .closedIssues(repo.getClosedIssues())
                .beginnerIssueCount(repo.getBeginnerIssueCount())
                .githubUrl(repo.getGithubUrl())
                .readmeUrl(repo.getReadmeUrl())
                .build();
    }

    public static RepoResponseDTO.RepoListDTO toRepoListDTO(List<Repo> repos) {
        return RepoResponseDTO.RepoListDTO.builder()
                .repoList(toTrendingDTOs(repos))
                .build();
    }

}
