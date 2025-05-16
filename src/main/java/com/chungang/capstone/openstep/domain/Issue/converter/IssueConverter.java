package com.chungang.capstone.openstep.domain.Issue.converter;

import com.chungang.capstone.openstep.domain.Issue.dto.IssueResponseDTO;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Task.entity.Task;

import java.util.List;
import java.util.stream.Collectors;

public class IssueConverter {
//    public static List<IssueResponseDTO.TrendingIssueDTO> toTrendingDTOs(List<Issue> issues) {
//        return issues.stream()
//                .map(issue -> IssueResponseDTO.TrendingIssueDTO.builder()
//                        .title(issue.getTitle())
//                        .body(issue.getBody())
//                        .url(issue.getGithubUrl())
//                        .createdAt(issue.getCreatedAt() != null ? issue.getCreatedAt().toString() : null)
//                        .updatedAt(issue.getUpdatedAt() != null ? issue.getUpdatedAt().toString() : null)
//                        .author(issue.getAuthor())
//                        .labels(issue.getLabels())
//                        .build())
//                .collect(Collectors.toList());
//    }
    public static List<IssueResponseDTO.TrendingIssueDTO> toTrendingDTOs(List<Issue> issues) {
        return issues.stream()
                .map(issue -> {
                    String filteredBody = (issue.getBody() == null || issue.getBody().length() < 200 || !issue.getBody().matches(".*[가-힣a-zA-Z].*"))
                            ? "내용 없음"
                            : issue.getBody();

                    return IssueResponseDTO.TrendingIssueDTO.builder()
                            .issueId(issue.getIssueId())
                            .title(issue.getTitle())
                            .body(filteredBody)
                            .summary(issue.getSummary())
                            .url(issue.getGithubUrl())
                            .createdAt(issue.getCreatedAt() != null ? issue.getCreatedAt().toString() : null)
                            .updatedAt(issue.getUpdatedAt() != null ? issue.getUpdatedAt().toString() : null)
                            .author(issue.getAuthor())
                            .labels(issue.getLabels())
                            .build();
                })
                .collect(Collectors.toList());
    }


    public static IssueResponseDTO.IssueDetailDTO toIssueDetailDTO(Issue issue) {
        return IssueResponseDTO.IssueDetailDTO.builder()
                .issueId(issue.getIssueId())
                .title(issue.getTitle())
                .body(issue.getBody())
                .summary(issue.getSummary())
                .author(issue.getAuthor())
                .createdAt(issue.getCreatedAt().toString())
                .updatedAt(issue.getUpdatedAt().toString())
                .labels(issue.getLabels())
                .url(issue.getGithubUrl())
                .build();
    }

    public static IssueResponseDTO.IssueAssignmentDTO toIssueAssignDTO(Task task) {
        return IssueResponseDTO.IssueAssignmentDTO.builder()
                .issueId(task.getIssue().getIssueId())
                .taskId(task.getTaskId())
                .title(task.getIssue().getTitle())
                .createdAt(task.getCreatedAt().toString())
                .updatedAt(task.getUpdatedAt().toString())
                .originalUrl(task.getIssue().getGithubUrl())
                .forkedUrl(task.getForkedUrl())
                .build();
    }
}
