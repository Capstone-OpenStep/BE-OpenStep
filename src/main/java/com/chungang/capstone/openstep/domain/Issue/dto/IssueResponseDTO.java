package com.chungang.capstone.openstep.domain.Issue.dto;

import lombok.*;

import java.util.List;

public class IssueResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IssueSimpleDTO {
        private Long issueId;
        private String title;
        private String body;
        private String summary;
        private String language;
        private String url;
        private String createdAt;
        private String updatedAt;
        private String author;
        private String authorAvatarUrl;
        private boolean isBookmarked;
        private List<String> labels;
        private String repoName;
        private String repoUrl;
    }


    @Getter
    @Builder
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IssueDetailDTO {
        private Long issueId;
        private Long repoId;
        private String title;
        private String body;
        private String summary;
        private String language;
        private String url;
        private String createdAt;
        private String updatedAt;
        private String author;
        private String authorAvatarUrl;
        private boolean isBookmarked;
        private List<String> labels;
        private String repoName;
        private String repoUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IssueDetailWithRepoDTO {
        private IssueDetailDTO issue;
        private RepoSummaryDTO repo;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RepoSummaryDTO {
        private Long repoId;
        private String repoName;
        private String summary;
        private String ownerName;
        private String ownerAvatarUrl;
        private String description;
        private String language;
        private int stars;
        private int watchers;
        private int forks;
        private int openIssues;
        private int closedIssues;
        private int beginnerIssueCount;
        private String githubUrl;
        private String readmeUrl;
    }


    @Builder
    public record IssueAssignmentDTO(
        Long issueId,
        Long taskId,
        String title,
        String originalUrl,
        String branchName,
        String forkedUrl,
        Boolean isAssigned,
        String createdAt,
        String updatedAt
    ) { }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IssueListDTO {
        private List<IssueSimpleDTO> issueList;
    }
}
