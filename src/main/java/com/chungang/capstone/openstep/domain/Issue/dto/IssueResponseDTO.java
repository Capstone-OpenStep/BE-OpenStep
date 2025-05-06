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
    public static class TrendingIssueDTO {
        private String title;
        private String body;
        private String url;
        private String createdAt;
        private String updatedAt;
        private String author;
        private List<String> labels;
    }

}
