package com.chungang.capstone.openstep.domain.Repo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RepoResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TrendingRepoDTO {
        private Long repoId;
        private String repoName;
        private String summary;
        private String ownerName;
        private String language;
        private Integer stars;
        private Integer watchers;
        private Integer forks;
        private Integer openIssues;
        private Integer closedIssues;
        private Integer beginnerIssueCount;
        private String githubUrl;
        private String description;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RepoDetailDTO {
        private Long repoId;
        private String repoName;
        private String summary;
        private String ownerName;
        private String description;
        private String language;
        private int stars;
        private int watchers;
        private int forks;
        private int openIssues;
        private int closedIssues;
        private Integer beginnerIssueCount;
        private String githubUrl;
        private String readmeUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RepoListDTO {
        private List<TrendingRepoDTO> repoList;
    }

}
