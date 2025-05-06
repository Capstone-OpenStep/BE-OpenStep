package com.chungang.capstone.openstep.domain.Issue.controller;

import com.chungang.capstone.openstep.domain.Issue.service.IssueQueryService;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.domain.Issue.converter.IssueConverter;
import com.chungang.capstone.openstep.domain.Issue.dto.IssueResponseDTO;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issues")
@Tag(name = "이슈 API", description = "GitHub 이슈 관련 API입니다.")
public class IssueController {

    private final IssueQueryService issueQueryService;

    // 트렌딩 이슈 목록 조회 API
    @GetMapping("/trending")
    @Operation(summary = "트렌딩 issue 조회 API", description = "현재 인기 있는 트렌딩한 오픈소스 이슈를 조회합니다.")
    public ApiResponse<List<IssueResponseDTO.TrendingIssueDTO>> getTrendingIssues() {
        List<IssueResponseDTO.TrendingIssueDTO> issues = issueQueryService.getTrendingIssues();
        return ApiResponse.onSuccess(SuccessStatus.ISSUE_GET_TRENDING_OK, issues);
    }

    // 특정 이슈 상세 조회
//    @GetMapping("/{issue-id}")
//    @Operation(summary = "특정 이슈 상세 조회 API", description = "특정 오픈소스 이슈의 상세 정보를 조회합니다.")
//    public ApiResponse<IssueResponseDTO.IssueDetailDTO> getIssueDetail(@PathVariable("issue-id") Long issueId) {
//        Issue issue = issueQueryService.getIssueById(issueId);
//        return ApiResponse.onSuccess(SuccessStatus.ISSUE_GET_DETAIL_OK, IssueConverter.toIssueDetailDTO(issue));
//    }

}
