package com.chungang.capstone.openstep.domain.Issue.controller;

import com.chungang.capstone.openstep.domain.Issue.service.IssueCommandService;
import com.chungang.capstone.openstep.domain.Issue.service.IssueQueryService;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.domain.Issue.converter.IssueConverter;
import com.chungang.capstone.openstep.domain.Issue.dto.IssueResponseDTO;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.global.security.util.SecurityUtils;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issues")
@Tag(name = "이슈 API", description = "GitHub 이슈 관련 API입니다.")
public class IssueController {

	private final IssueQueryService issueQueryService;
	private final IssueCommandService issueCommandService;

	// 트렌딩 이슈 목록 조회 API
	@GetMapping("/trending")
	@Operation(summary = "트렌딩 issue 조회 API", description = "현재 인기 있는 트렌딩한 오픈소스 이슈를 조회합니다.")
	public ApiResponse<List<IssueResponseDTO.IssueSimpleDTO>> getTrendingIssues() {
		List<IssueResponseDTO.IssueSimpleDTO> issues = issueQueryService.getTrendingIssues();
		return ApiResponse.onSuccess(SuccessStatus.ISSUE_GET_TRENDING_OK, issues);
	}

	// 특정 이슈 상세 조회
	@GetMapping("/{issue-id}")
	@Operation(summary = "특정 이슈 상세 조회 API", description = "특정 오픈소스 이슈의 상세 정보를 조회합니다.")
	public ApiResponse<IssueResponseDTO.IssueDetailDTO> getIssueDetail(@PathVariable("issue-id") Long issueId) {
		Issue issue = issueQueryService.getIssueById(issueId);
		return ApiResponse.onSuccess(SuccessStatus.ISSUE_GET_DETAIL_OK, IssueConverter.toIssueDetailDTO(issue));
	}

	// 특정 이슈 사용자 할당( 레포지토리 fork 후 브랜치 생성 코드 반환)
	@PostMapping("/{issue-id}/assign")
	@Operation(summary = "특정 이슈 사용자 할당 API", description = "특정 오픈소스 이슈를 사용자의 task로 할당합니다.")
	public ApiResponse<IssueResponseDTO.IssueAssignmentDTO> assignIssueToUser(
		@PathVariable("issue-id") Long issueId) {
		Member member = SecurityUtils.getCurrentMember();
		Task task = issueCommandService.makeTask(member, issueId);
		return ApiResponse.onSuccess(SuccessStatus.TASK_ASSIGN_OK, IssueConverter.toIssueAssignDTO(task));
	}

	// 사용자 맞춤 이슈 추천
	@GetMapping("/suggest")
	@Operation(summary = "사용자 맞춤 이슈 추천 API", description = "사용자의 관심사에 맞는 오픈소스 이슈를 추천합니다.")
	public ApiResponse<IssueResponseDTO.IssueListDTO> suggestIssues() {
		Member member = SecurityUtils.getCurrentMember();
		List<Issue> issues = issueQueryService.getSuggestedIssues(member);
		return ApiResponse.onSuccess(SuccessStatus.ISSUE_GET_SUGGEST_OK, IssueConverter.toIssueListDTO(issues));
	}

}
