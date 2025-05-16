package com.chungang.capstone.openstep.domain.Issue.service;

import com.chungang.capstone.openstep.domain.Github.dto.GitHubIssueResponse;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Issue.repository.IssueRepository;
import com.chungang.capstone.openstep.domain.Issue.dto.IssueResponseDTO;
import com.chungang.capstone.openstep.domain.Github.service.GitHubGraphQLService;
import com.chungang.capstone.openstep.domain.Issue.converter.IssueConverter;
import com.chungang.capstone.openstep.domain.OpenAI.service.OpenAIService;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Repo.repository.RepoRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.IssueHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class IssueQueryService {

    private final IssueRepository issueRepository;

    private final GitHubGraphQLService gitHubGraphQLService;
    private final RepoRepository repoRepository;
    private final OpenAIService openAIService;

    public List<IssueResponseDTO.TrendingIssueDTO> getTrendingIssues() {
        List<Repo> repos = repoRepository.findAll(); // DB에 있는 5개 레포 사용
        List<Issue> allIssues = new ArrayList<>();

        for (Repo repo : repos) {
            String owner = repo.getOwnerName();
            String repoName = repo.getRepoName();

            GitHubIssueResponse issueResponse = gitHubGraphQLService.fetchIssuesByRepo(owner, repoName);

            if (issueResponse != null && issueResponse.getData().getRepository() != null) {
                var issueNodes = issueResponse.getData().getRepository().getIssues().getNodes();

                List<Issue> savedIssues = issueNodes.stream()
                        .map(node -> saveIfNotExists(node, repo))
                        .filter(Objects::nonNull)
                        .toList();

                allIssues.addAll(savedIssues);
                System.out.println("📌 " + owner + "/" + repoName + " 의 이슈 수: " + issueNodes.size());
            }
        }

        return IssueConverter.toTrendingDTOs(allIssues);
    }

    private Issue saveIfNotExists(GitHubIssueResponse.IssueNode node, Repo repo) {
        if (node.getTitle() == null || node.getTitle().length() < 1) return null;

//        String rawBody = node.getBody();
//        String body = (rawBody == null || rawBody.length() < 20 || !rawBody.matches(".*[가-힣a-zA-Z].*"))
//                ? "내용 없음"
//                : rawBody;

        String body = node.getBody();

        List<String> labels = node.getLabels() != null
                ? node.getLabels().getNodes().stream().map(GitHubIssueResponse.LabelNode::getName).toList()
                : Collections.emptyList();

        boolean isGoodLabel = labels.stream().anyMatch(label ->
                label.toLowerCase().contains("good first issue") || label.toLowerCase().contains("help wanted"));

        if (!isGoodLabel) return null;

        return issueRepository.findByGithubUrl(node.getUrl())
                .orElseGet(() -> issueRepository.save(toIssueEntity(node, repo, body)));
    }

//    private Issue saveIfNotExists(GitHubIssueResponse.IssueNode node, Repo repo) {
//        if (node.getTitle() == null || node.getTitle().length() < 1) return null;
//
//        String rawBody = node.getBody(); // 그대로 저장
//        List<String> labels = node.getLabels() != null
//                ? node.getLabels().getNodes().stream().map(GitHubIssueResponse.LabelNode::getName).toList()
//                : Collections.emptyList();
//
//        boolean isGoodLabel = labels.stream().anyMatch(label ->
//                label.toLowerCase().contains("good first issue") || label.toLowerCase().contains("help wanted"));
//
//        if (!isGoodLabel) return null;
//
//        return issueRepository.findByGithubUrl(node.getUrl())
//                .orElseGet(() -> issueRepository.save(toIssueEntity(node, repo, rawBody)));
//    }


    private Issue toIssueEntity(GitHubIssueResponse.IssueNode node, Repo repo, String body) {
        String summary = openAIService.summarizeIssue(node.getTitle(), body);
        String refinedSummary = openAIService.rewriteNaturalKorean(summary);

        return Issue.builder()
                .repo(repo)
                .title(node.getTitle())
                .body(body)
                .summary(refinedSummary)
                .githubUrl(node.getUrl())
                .author(node.getAuthor() != null ? node.getAuthor().getLogin() : "unknown")
                .labels(node.getLabels() != null
                        ? node.getLabels().getNodes().stream().map(GitHubIssueResponse.LabelNode::getName).toList()
                        : Collections.emptyList())
                .createdAt(node.getCreatedAt() != null ? OffsetDateTime.parse(node.getCreatedAt()).toLocalDateTime() : null)
                .updatedAt(node.getUpdatedAt() != null ? OffsetDateTime.parse(node.getUpdatedAt()).toLocalDateTime() : null)
                .status("OPEN")
                .build();
    }


    public Issue getIssueById(Long issueId) {
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueHandler(ErrorStatus.ISSUE_NOT_FOUND));
    }

}
