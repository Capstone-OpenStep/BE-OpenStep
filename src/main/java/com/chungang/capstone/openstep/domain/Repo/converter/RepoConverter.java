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
                        .ownerName(repo.getOwnerName())
                        .language(repo.getLanguage())
                        .stars(repo.getStars())
                        .watchers(repo.getWatchers())
                        .forks(repo.getForks())
                        .openIssues(repo.getOpenIssues())
                        .closedIssues(repo.getClosedIssues())
                        .githubUrl(repo.getGithubUrl())
                        .description(repo.getDescription())
                        .build())
                .toList();
    }


    public static RepoResponseDTO.RepoDetailDTO toRepoDetailDTO(Repo repo) {
        return RepoResponseDTO.RepoDetailDTO.builder()
                .repoId(repo.getRepoId())
                .repoName(repo.getRepoName())
                .ownerName(repo.getOwnerName())
                .description(repo.getDescription())
                .language(repo.getLanguage())
                .stars(repo.getStars())
                .watchers(repo.getWatchers())
                .forks(repo.getForks())
                .openIssues(repo.getOpenIssues())
                .closedIssues(repo.getClosedIssues())
                .githubUrl(repo.getGithubUrl())
                .readmeUrl(repo.getReadmeUrl())
                .build();
    }
}
