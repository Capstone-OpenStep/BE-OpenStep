package com.chungang.capstone.openstep.domain.OpenAI.controller;

import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Issue.repository.IssueRepository;
import com.chungang.capstone.openstep.domain.OpenAI.service.OpenAIService;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Repo.repository.RepoRepository;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.IssueHandler;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.RepoHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/summary")
@Tag(name = "요약 API", description = "오픈소스 레포지토리 및 이슈 요약 API입니다.")
public class SummaryController {

    private final OpenAIService openAIService;
    private final RepoRepository repoRepository;
    private final IssueRepository issueRepository;

    // 레포지토리 요약 API
    @GetMapping("/repo/{repo-id}")
    @Operation(summary = "레포지토리 요약 API", description = "특정 레포지토리의 설명과 README 내용을 요약합니다.")
    public ApiResponse<String> summarizeRepo(
            @Parameter(description = "요약할 레포지토리의 ID", required = true)
            @PathVariable("repo-id") Long repoId) {

        Repo repo = repoRepository.findById(repoId)
                .orElseThrow(() -> new RepoHandler(ErrorStatus.REPO_NOT_FOUND));

        String summary = openAIService.summarizeRepo(repo.getDescription(), repo.getReadmeUrl());
        String refinedSummary = openAIService.rewriteNaturalKorean(summary);
        return ApiResponse.onSuccess(SuccessStatus.REPO_SUMMARY_OK, refinedSummary);
    }

    // 이슈 요약 API
    @GetMapping("/issue/{issue-id}")
    @Operation(summary = "이슈 요약 API", description = "특정 이슈의 제목과 내용을 요약합니다.")
    public ApiResponse<String> summarizeIssue(
            @Parameter(description = "요약할 이슈의 ID", required = true)
            @PathVariable("issue-id") Long issueId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueHandler(ErrorStatus.ISSUE_NOT_FOUND));

        String summary = openAIService.summarizeIssue(issue.getTitle(), issue.getBody());
        String refinedSummary = openAIService.rewriteNaturalKorean(summary);
        return ApiResponse.onSuccess(SuccessStatus.ISSUE_SUMMARY_OK, refinedSummary);
    }
}
