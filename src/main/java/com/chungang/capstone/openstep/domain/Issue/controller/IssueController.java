package com.chungang.capstone.openstep.domain.Issue.controller;

import com.chungang.capstone.openstep.domain.Github.dto.GitHubIssueResponse;
import com.chungang.capstone.openstep.domain.Github.service.GitHubGraphQLService;
import com.chungang.capstone.openstep.domain.Issue.service.IssueCommandService;
import com.chungang.capstone.openstep.domain.Issue.service.IssueQueryService;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.OpenAI.service.OpenAIService;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.common.InterestLanguage;
import com.chungang.capstone.openstep.domain.common.UpdatePeriod;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.domain.Issue.converter.IssueConverter;
import com.chungang.capstone.openstep.domain.Issue.dto.IssueResponseDTO;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.IssueHandler;
import com.chungang.capstone.openstep.global.security.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/issues")
@Tag(name = "이슈 API", description = "GitHub 이슈 관련 API입니다.")
public class IssueController {

	private final IssueQueryService issueQueryService;
	private final IssueCommandService issueCommandService;
	private final GitHubGraphQLService gitHubGraphQLService;
	private final OpenAIService openAIService;

	// 트렌딩 이슈 목록 조회 API
	@GetMapping("/trending")
	@Operation(summary = "트렌딩 issue 조회 API", description = "현재 인기 있는 트렌딩한 오픈소스 이슈를 조회합니다.")
	public ApiResponse<IssueResponseDTO.IssueListDTO> getTrendingIssues(@Parameter(description = "페이지 번호 (0~3)") @RequestParam(defaultValue = "0") @Min(0) @Max(3) int page) {
		List<Issue> issues = issueQueryService.getTrendingIssues(PageRequest.of(page, 5));
		Member member = SecurityUtils.getCurrentMemberOrNull();
		List<Long> bookmarkedIds = (member != null) ? issueQueryService.getBookmarkedIssueIds(member.getMemberId()) : List.of(); // 비로그인시에는 빈 리스트 반환
		return ApiResponse.onSuccess(SuccessStatus.ISSUE_GET_TRENDING_OK, IssueConverter.toIssueListDTO(issues, bookmarkedIds));
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
		IssueResponseDTO.IssueAssignmentDTO task = issueCommandService.makeTask(member, issueId);
		return ApiResponse.onSuccess(SuccessStatus.TASK_ASSIGN_OK, task);
	}

	// 사용자 맞춤 이슈 추천
	@GetMapping("/suggest")
	@Operation(summary = "사용자 맞춤 이슈 추천 API", description = "사용자의 관심사에 맞는 오픈소스 이슈를 추천합니다.")
	public ApiResponse<IssueResponseDTO.IssueListDTO> suggestIssues(@Parameter(description = "페이지 번호 (0~3)") @RequestParam(defaultValue = "0") @Min(0) @Max(3) int page) {
		Member member = SecurityUtils.getCurrentMember();
		List<Issue> issues = issueQueryService.getSuggestedIssues(member, PageRequest.of(page, 5));
		List<Long> bookmarkedIds = issueQueryService.getBookmarkedIssueIds(member.getMemberId());
		return ApiResponse.onSuccess(SuccessStatus.ISSUE_GET_SUGGEST_OK, IssueConverter.toIssueListDTO(issues, bookmarkedIds));
	}

	// 키워드로 이슈 검색
//	@GetMapping("/search/keyword")
//	@Operation(summary = "키워드로 이슈 검색 API", description = "키워드로 이슈를 검색합니다.")
//	public ApiResponse<IssueResponseDTO.IssueListDTO> searchIssuesByKeyword(@RequestParam Optional<String> search,
//																			@RequestParam InterestLanguage interestLanguage) {
//		Member member = SecurityUtils.getCurrentMember();
//		List<Issue> issues = issueQueryService.getIssuesByKeyword(Optional.of(search.orElse("")));
//		List<Long> bookmarkedIds = issueQueryService.getBookmarkedIssueIds(member.getMemberId());
//		return ApiResponse.onSuccess(SuccessStatus.ISSUE_SEARCH_BY_KEYWORD_OK, IssueConverter.toIssueListDTO(issues, bookmarkedIds));
//	}
	@GetMapping("/search/keyword")
	@Operation(summary = "키워드 + 필터 기반 이슈 검색", description = "키워드, 언어, 업데이트 기간 필터로 GitHub 이슈를 검색합니다.")
	public ApiResponse<IssueResponseDTO.IssueListDTO> searchIssuesByKeyword(
			@RequestParam String search,
			@RequestParam(required = false) List<InterestLanguage> languages,
			@RequestParam(required = false) UpdatePeriod updatePeriod,
			@Parameter(description = "페이지 번호 (0~3)") @RequestParam(defaultValue = "0") @Min(0) @Max(3) int page
	) {
		Member member = SecurityUtils.getCurrentMemberOrNull();
		List<Issue> issues = issueQueryService.searchGitHubIssuesByKeywordAndFilters(search, languages, updatePeriod, PageRequest.of(page, 5));
		List<Long> bookmarkedIds = (member != null) ? issueQueryService.getBookmarkedIssueIds(member.getMemberId()) : List.of();
		return ApiResponse.onSuccess(SuccessStatus.ISSUE_SEARCH_BY_KEYWORD_OK,
				IssueConverter.toIssueListDTO(issues, bookmarkedIds));
	}

	@GetMapping("/detail-by-url")
	@Operation(summary = "GitHub URL 기반 이슈 + 레포 상세 조회", description = "GitHub URL로 이슈의 상세 정보를 조회하고, 이슈가 속한 레포지토리 정보도 함께 제공합니다.")
	public ApiResponse<IssueResponseDTO.IssueDetailWithRepoDTO> getIssueDetailByUrl(@RequestParam String url) {
		IssueResponseDTO.IssueDetailWithRepoDTO dto = issueQueryService.getIssueDetailWithRepoByUrl(url);
		try {
			String raw = openAIService.summarizeIssue(dto.getIssue().getTitle(), dto.getIssue().getBody());
			dto.getIssue().setSummary(openAIService.rewriteNaturalKorean(raw));
		} catch (Exception e) {
			log.warn("[SUMMARY] 요약 실패: {}", url, e);
			dto.getIssue().setSummary("요약을 생성할 수 없습니다.");
		}
		return ApiResponse.onSuccess(SuccessStatus.ISSUE_GET_DETAIL_OK, dto);
	}









}
