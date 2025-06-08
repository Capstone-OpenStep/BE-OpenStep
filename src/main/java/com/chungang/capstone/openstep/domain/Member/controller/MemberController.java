package com.chungang.capstone.openstep.domain.Member.controller;

import java.util.List;

import com.chungang.capstone.openstep.domain.Github.dto.GitHubUserProfile;
import com.chungang.capstone.openstep.domain.Member.converter.MemberConverter;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.chungang.capstone.openstep.domain.Github.dto.PullRequestResponse;
import com.chungang.capstone.openstep.domain.Github.service.GitHubGraphQLService;
import com.chungang.capstone.openstep.domain.Member.dto.MemberRequestDTO;
import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.domain.Member.service.MemberCommandService;
import com.chungang.capstone.openstep.domain.Member.service.MemberQueryService;
import com.chungang.capstone.openstep.domain.achievement.dto.AchievementDTO;
import com.chungang.capstone.openstep.domain.achievement.entity.MemberAchievement;
import com.chungang.capstone.openstep.domain.achievement.service.AchievementService;
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
	private final AchievementService achievementService;

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

	@PostMapping("/select/languages")
	@Operation(summary = "관심언어(languages) 선택 API", description = "사용자의 기술스택 내역을 수정합니다.")
	public ApiResponse<MemberResponseDTO.LanguagesRes> selectInterestLanguages(@Valid @RequestBody MemberRequestDTO.UpdateLanguagesReq languagesReq) {
		Long memberId= SecurityUtils.getCurrentMemberId();
		MemberResponseDTO.LanguagesRes languagesRes = memberCommandService.selectInterestLanguages(memberId, languagesReq);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_SELECT_INTEREST_LANGUAGES_OK, languagesRes);
	}

	@PostMapping("/select/domains")
	@Operation(summary = "관심분야(domains) 선택 API", description = "사용자가 관심있는 분야를 수정합니다.")
	public ApiResponse<MemberResponseDTO.DomainsRes> selectInterestDomains(
																   @Valid @RequestBody MemberRequestDTO.UpdateDomainsReq domainsReq) {
		Long memberId= SecurityUtils.getCurrentMemberId();
		MemberResponseDTO.DomainsRes domainsRes = memberCommandService.selectInterestDomains(memberId, domainsReq);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_SELECT_INTEREST_DOMAINS_OK, domainsRes);
	}

	@GetMapping("/languages")
	@Operation(summary = "사용자 관심언어(languages) 조회 API", description = "사용자의 관심언어를 조회합니다.")
	public ApiResponse<MemberResponseDTO.LanguagesRes> getMemberLanguages(){
		Long memberId= SecurityUtils.getCurrentMemberId();
		log.info("memberId={}",memberId);
		MemberResponseDTO.LanguagesRes languageRes =memberQueryService.getLanguages(memberId);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_GET_INTEREST_LANGUAGES_OK, languageRes);
	}

	@GetMapping("/domains")
	@Operation(summary = "사용자 관심분야(domains) 조회 API", description = "사용자의 관심분야를 조회합니다.")
	public ApiResponse<MemberResponseDTO.DomainsRes> getMemberDomains(){
		Long memberId= SecurityUtils.getCurrentMemberId();
		log.info("memberId={}",memberId);
		MemberResponseDTO.DomainsRes domainsRes =memberQueryService.getDomains(memberId);
		return ApiResponse.onSuccess(SuccessStatus.MEMBER_GET_INTEREST_DOMAINS_OK, domainsRes);
	}

	// // without token
	// @GetMapping("/{memberId}/languages")
	// @Operation(summary = "사용자 관심 언어 조회 API", description = "사용자의 관심 언어를 조회합니다.")
	// public ApiResponse<MemberResponseDTO.LanguagesRes> getMemberLanguages(@PathVariable Long memberId) {
	// 	//log.info("getMemberLanguages for memberId={}", memberId);
	// 	return ApiResponse.onSuccess(SuccessStatus.MEMBER_GET_INTEREST_LANGUAGES_OK,
	// 			memberQueryService.getLanguages(memberId));
	// }

	// without token
	// @GetMapping("/{memberId}/domains")
	// @Operation(summary = "사용자 관심 분야 조회 API", description = "사용자의 관심 분야를 조회합니다.")
	// public ApiResponse<MemberResponseDTO.DomainsRes> getMemberDomains(@PathVariable Long memberId) {
	// 	//log.info("getMemberDomains for memberId={}", memberId);
	// 	return ApiResponse.onSuccess(SuccessStatus.MEMBER_GET_INTEREST_DOMAINS_OK,
	// 			memberQueryService.getDomains(memberId));
	// }


	@Operation(summary = "기여내역 조회 API", description = "사용자의 기여내역을 조회합니다.")
	@GetMapping("/contributions")
	public ApiResponse<List<PullRequestResponse.PullRequestRes>> getContributions(){
		String githubId=SecurityUtils.getCurrentMemberGithubId();
		log.info("githubId={}",githubId);
		List<PullRequestResponse.PullRequestRes> contributions = gitHubGraphQLService.fetchMyPullRequestsWithIssues(githubId);
		return ApiResponse.onSuccess(SuccessStatus.ISSUE_GET_DETAIL_OK, contributions);
	}

	@GetMapping("/github/profile/realtime")
	@Operation(summary = "GitHub 프로필 실시간 조회", description = "GitHub GraphQL API를 통해 실시간으로 프로필 정보를 조회합니다.")
	public ApiResponse<MemberResponseDTO.GitHubProfileDTO> getGitHubProfileRealtime() {
		Long memberId = SecurityUtils.getCurrentMemberId();
		Member member = memberQueryService.getMemberById(memberId);
		String accessToken = member.getGithubAccessToken();

		GitHubUserProfile profile = gitHubGraphQLService.fetchAuthenticatedUserProfile(accessToken);

		MemberResponseDTO.GitHubProfileDTO dto = MemberResponseDTO.GitHubProfileDTO.builder()
				.githubId(profile.getLogin())
				.email(profile.getEmail())
				.avatarUrl(profile.getAvatarUrl())
				.location(profile.getLocation())
				.profileUrl(profile.getUrl())
				.followersCount(profile.getFollowersCount())
				.followingCount(profile.getFollowingCount())
				.build();

		return ApiResponse.onSuccess(SuccessStatus.MEMBER_GET_GITHUB_PROFILE_OK, dto);
	}

	//사용자의 모든 업적 조회
	@GetMapping("/achievements")
	@Operation(summary = "사용자의 모든 업적 조회", description = "사용자가 달성한 모든 업적을 조회합니다.")
	public ApiResponse<List<AchievementDTO>> getMemberAchievements() {
		Long memberId= SecurityUtils.getCurrentMemberId();
		List<MemberAchievement> achievements = achievementService.getMemberUnlockedAchievements(memberId);
		return ApiResponse.onSuccess(SuccessStatus.ACHIEVEMENT_GET_ALL_OK,achievements.stream().map(AchievementDTO::from).toList());
	}

}
