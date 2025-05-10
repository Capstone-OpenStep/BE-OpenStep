package com.chungang.capstone.openstep.domain.Member.controller;

import java.util.List;

import com.chungang.capstone.openstep.domain.Member.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.chungang.capstone.openstep.domain.Github.dto.PullRequestResponse;
import com.chungang.capstone.openstep.domain.Github.service.GitHubGraphQLService;
import com.chungang.capstone.openstep.domain.Member.dto.MemberRequestDTO;
import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.domain.Member.service.MemberCommandService;
import com.chungang.capstone.openstep.domain.Member.service.MemberQueryService;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;
import com.chungang.capstone.openstep.global.security.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "Member",description = "Member 정보 API")
@Slf4j
public class MemberController {

	private final AuthService authService;
	private final MemberQueryService memberQueryService;
	private final MemberCommandService memberCommandService;
	private final GitHubGraphQLService gitHubGraphQLService;

	@PostMapping("/sign_up")
	@Operation(summary = "회원가입", description = "회원가입을 진행합니다.")
	public ApiResponse<String> signUp(@Valid @RequestBody MemberRequestDTO.MemberSignUpRequestDTO request) {
		String response = authService.signUp(request);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_SIGN_UP_OK, response);
	}

	@PostMapping("/login")
	@Operation(summary = "로그인", description = "로그인을 진행합니다.")
	public ApiResponse<MemberResponseDTO.MemberTokenResponseDTO> login(@Valid @RequestBody MemberRequestDTO.MemberLoginRequestDTO request) {
		MemberResponseDTO.MemberTokenResponseDTO response = authService.login(request);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_LOGIN_OK, response);
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	public ApiResponse<String> logout(@Valid @RequestBody MemberRequestDTO.refreshRequestDTO request) {
		String response = authService.logout(request);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_LOGOUT_OK, response);
	}

	@PostMapping("/refresh")
	@Operation(summary = "액세스 토큰 재할당")
	public ApiResponse<MemberResponseDTO.TokenRefreshResponseDTO> refresh(@Valid @RequestBody MemberRequestDTO.refreshRequestDTO request) {
		MemberResponseDTO.TokenRefreshResponseDTO response = authService.refresh(request);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_UPDATE_ACCESS_TOKEN_OK, response);
	}


	@Operation(summary = "관심사(domain) 조회 API", description = "사용자의 관심사(도메인)을 조회합니다.")
	@GetMapping("/domains")
	public ApiResponse<MemberResponseDTO.DomainsRes> getDomains(){
		Long memberId= SecurityUtils.getCurrentMemberId();
		log.info("memberId={}",memberId);
		MemberResponseDTO.DomainsRes domainsRes =memberQueryService.getDomains(memberId);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_GET_INTERESTS_OK, domainsRes);
	}

	@Operation(summary = "관심사(domain) 수정 API", description = "사용자의 관심사(도메인)내역을 수정합니다.")
	@PatchMapping("/domains")
	public ApiResponse<MemberResponseDTO.DomainsRes> updateDomains(@RequestBody MemberRequestDTO.UpdateDomainsReq domainsReq){
		Long memberId= SecurityUtils.getCurrentMemberId();
		log.info("memberId={}",memberId);
		MemberResponseDTO.DomainsRes domainsRes =memberCommandService.updateDomains(memberId,domainsReq);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_PATCH_INTERESTS_OK, domainsRes);
	}

	@Operation(summary = "기술스택 조회 API", description = "사용자의 기술스택 내역을 조회합니다.")
	@GetMapping("/skills")
	public ApiResponse<MemberResponseDTO.SkillsRes> getSkills(){
		Long memberId= SecurityUtils.getCurrentMemberId();
		log.info("memberId={}",memberId);
		MemberResponseDTO.SkillsRes skillRes =memberQueryService.getSkills(memberId);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_GET_SKILLS_OK, skillRes);
	}

	@Operation(summary = "기술스택 수정 API", description = "사용자의 기술스택 내역을 수정합니다.")
	@PatchMapping("/skills")
	public ApiResponse<MemberResponseDTO.SkillsRes> updateSkills(@RequestBody MemberRequestDTO.UpdateSkillsReq skillsReq){
		Long memberId= SecurityUtils.getCurrentMemberId();
		log.info("memberId={}",memberId);
		MemberResponseDTO.SkillsRes skillsRes =memberCommandService.updateSkills(memberId,skillsReq);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_PATCH_SKILLS_OK, skillsRes);
	}

	@Operation(summary = "기여내역 조회 API", description = "사용자의 기여내역을 조회합니다.")
	@GetMapping("/contributions")
	public ApiResponse<List<PullRequestResponse.PullRequestRes>> getContributions(){
		String githubId=SecurityUtils.getCurrentMemberGithubId();
		log.info("githubId={}",githubId);
		List<PullRequestResponse.PullRequestRes> contributions = gitHubGraphQLService.fetchMyPullRequestsWithIssues(githubId);
		return ApiResponse.onSuccess(SuccessStatus.ISSUE_GET_DETAIL_OK, contributions);
	}
}
