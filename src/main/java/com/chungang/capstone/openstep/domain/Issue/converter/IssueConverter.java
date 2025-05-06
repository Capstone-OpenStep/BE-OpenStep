package com.chungang.capstone.openstep.domain.Issue.converter;

import com.chungang.capstone.openstep.domain.Github.dto.GitHubIssueResponse;
import com.chungang.capstone.openstep.domain.Issue.dto.IssueResponseDTO;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IssueConverter {
    public static List<IssueResponseDTO.TrendingIssueDTO> toTrendingDTOs(List<Issue> issues) {
        return issues.stream()
                .map(issue -> IssueResponseDTO.TrendingIssueDTO.builder()
                        .title(issue.getTitle())
                        .body(issue.getBody())
                        .url(issue.getGithubUrl())
                        .createdAt(issue.getCreatedAt() != null ? issue.getCreatedAt().toString() : null)
                        .updatedAt(issue.getUpdatedAt() != null ? issue.getUpdatedAt().toString() : null)
                        .author(issue.getAuthor())
                        .labels(issue.getLabels())
                        .build())
                .collect(Collectors.toList());
    }

}
