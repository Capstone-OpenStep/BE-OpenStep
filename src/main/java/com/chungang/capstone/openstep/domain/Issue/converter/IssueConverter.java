package com.chungang.capstone.openstep.domain.Issue.converter;

import com.chungang.capstone.openstep.domain.Github.dto.GitHubIssueResponse;
import com.chungang.capstone.openstep.domain.Issue.dto.IssueResponseDTO;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Task.entity.Task;

import java.time.OffsetDateTime;
import java.util.*;
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
    public static List<IssueResponseDTO.IssueSimpleDTO> toIssueSimpleDTOs(List<Issue> issues, List<Long> bookmarkedIds) {
        Set<Long> bookmarkedSet = new HashSet<>(bookmarkedIds);

        return issues.stream()
                .map(issue -> {
                    boolean isBookmarked = bookmarkedSet.contains(issue.getIssueId());
                    String filteredBody = (issue.getBody() == null || issue.getBody().length() < 500 || !issue.getBody().matches(".*[가-힣a-zA-Z].*"))
                            ? "내용 없음"
                            : issue.getBody();

                    return IssueResponseDTO.IssueSimpleDTO.builder()
                            .issueId(issue.getIssueId())
                            .title(issue.getTitle())
                            .body(filteredBody)
                            .summary(issue.getSummary())
                            .language(issue.getLanguage())
                            .url(issue.getGithubUrl())
                            .createdAt(issue.getCreatedAt() != null ? issue.getCreatedAt().toString() : null)
                            .updatedAt(issue.getUpdatedAt() != null ? issue.getUpdatedAt().toString() : null)
                            .author(issue.getAuthor())
                            .authorAvatarUrl(issue.getAuthorAvatarUrl())
                            .isBookmarked(isBookmarked)
                            .labels(issue.getLabels())
                            .repoName(issue.getRepo() != null ? issue.getRepo().getRepoName() : null)
                            .repoUrl(issue.getRepo() != null ? issue.getRepo().getGithubUrl() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }


    public static IssueResponseDTO.IssueDetailDTO toIssueDetailDTO(Issue issue) {
        return IssueResponseDTO.IssueDetailDTO.builder()
                .issueId(issue.getIssueId())
                .repoId(issue.getRepo().getRepoId())
                .title(issue.getTitle())
                .body(issue.getBody())
                .summary(issue.getSummary())
                .language(issue.getLanguage())
                .author(issue.getAuthor())
                .authorAvatarUrl(issue.getAuthorAvatarUrl())
                .createdAt(issue.getCreatedAt() != null ? issue.getCreatedAt().toString() : null)
                .updatedAt(issue.getUpdatedAt() != null ? issue.getUpdatedAt().toString() : null)
                .labels(issue.getLabels())
                .url(issue.getGithubUrl())
                .repoName(issue.getRepo() != null ? issue.getRepo().getRepoName() : null)
                .build();
    }

    public static List<IssueResponseDTO.IssueDetailDTO> toIssueDetailDTOs(List<Issue> issues) {
        return issues.stream()
                .map(IssueConverter::toIssueDetailDTO)
                .collect(Collectors.toList());
    }

    public static IssueResponseDTO.IssueListDTO toIssueListDTO(List<Issue> issues, List<Long> bookmarkedIds) {
        return IssueResponseDTO.IssueListDTO.builder()
                .issueList(toIssueSimpleDTOs(issues, bookmarkedIds))
                .build();
    }


    public static IssueResponseDTO.IssueAssignmentDTO toIssueAssignDTO(Task task,boolean isAlreadyAssigned) {
        return IssueResponseDTO.IssueAssignmentDTO.builder()
                .issueId(task.getIssue().getIssueId())
                .taskId(task.getTaskId())
                .title(task.getIssue().getTitle())
                .createdAt(task.getCreatedAt().toString())
                .updatedAt(task.getUpdatedAt().toString())
                .originalUrl(task.getIssue().getGithubUrl())
                .forkedUrl(task.getForkedUrl())
                .branchName(task.getBranchName())
                .isAssigned(isAlreadyAssigned)
                .build();
    }

//    public static Issue fromGitHubIssueNode(GitHubIssueResponse.IssueNode node) {
//        return Issue.builder()
//                .title(node.getTitle())
//                .body(Optional.ofNullable(node.getBody()).orElse("내용 없음"))
//                .summary(null)
//                .githubUrl(node.getUrl())
//                .author(node.getAuthor() != null ? node.getAuthor().getLogin() : "unknown")
//                .authorAvatarUrl(node.getAuthor() != null ? node.getAuthor().getAvatarUrl() : null)
//                .createdAt(OffsetDateTime.parse(node.getCreatedAt()).toLocalDateTime())
//                .updatedAt(OffsetDateTime.parse(node.getUpdatedAt()).toLocalDateTime())
//                .labels(node.getLabels() != null
//                        ? node.getLabels().getNodes().stream().map(GitHubIssueResponse.LabelNode::getName).toList()
//                        : Collections.emptyList())
//                .state("OPEN")
//                .language(node.getPrimaryLanguage() != null ? node.getPrimaryLanguage().getName() : null)
//                .build();
//    }

    public static Issue fromGitHubIssueNode(GitHubIssueResponse.IssueNode node) {
        GitHubIssueResponse.RepoInfo repoInfo = node.getRepoInfo();
        if (repoInfo == null) return null;

        String repoName = repoInfo != null ? repoInfo.getName() : null;
        String ownerName = (repoInfo != null && repoInfo.getOwner() != null)
                ? repoInfo.getOwner().getLogin() : null;

        String githubUrl = (repoInfo != null && repoInfo.getNameWithOwner() != null)
                ? "https://github.com/" + repoInfo.getNameWithOwner()
                : null;

        Repo repo = Repo.builder()
                .repoName(repoName)
                .githubUrl(githubUrl)
                .ownerName(ownerName)
                .ownerAvatarUrl((repoInfo != null && repoInfo.getOwner() != null)
                        ? repoInfo.getOwner().getAvatarUrl()
                        : null)
                .build();

        return Issue.builder()
                .title(node.getTitle())
                .body(Optional.ofNullable(node.getBody()).orElse("내용 없음"))
                .summary(null)
                .githubUrl(node.getUrl())
                .author(node.getAuthor() != null ? node.getAuthor().getLogin() : "unknown")
                .authorAvatarUrl(node.getAuthor() != null ? node.getAuthor().getAvatarUrl() : null)
                .createdAt(OffsetDateTime.parse(node.getCreatedAt()).toLocalDateTime())
                .updatedAt(OffsetDateTime.parse(node.getUpdatedAt()).toLocalDateTime())
                .labels(node.getLabels() != null
                        ? node.getLabels().getNodes().stream().map(GitHubIssueResponse.LabelNode::getName).toList()
                        : Collections.emptyList())
                .state(node.getState())
                .language(node.getPrimaryLanguage() != null ? node.getPrimaryLanguage().getName() : null)
                .repo(repo)
                .build();
    }


}
