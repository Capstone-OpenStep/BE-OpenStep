package com.chungang.capstone.openstep.domain.Issue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    }


    @Getter
    @Builder
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
