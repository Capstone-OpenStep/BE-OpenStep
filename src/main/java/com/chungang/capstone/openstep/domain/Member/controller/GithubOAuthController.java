package com.chungang.capstone.openstep.domain.Member.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import com.chungang.capstone.openstep.domain.Member.converter.MemberConverter;
import com.chungang.capstone.openstep.domain.Member.dto.GithubOAuthDTO;
import com.chungang.capstone.openstep.domain.Member.dto.LoginResult;
import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.domain.Member.service.GithubOauthService;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.AuthException;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/github/auth")
@Tag(name = "GithubOauth",description = "깃허브 로그인 관련 API")
@Slf4j
public class GithubOAuthController {

	private final GithubOauthService githubOauthService;

	@GetMapping("/callback")
	public ApiResponse<MemberResponseDTO.MemberRes> getUserByGithub(@RequestParam("code") String code){
		try {
			String accessToken = githubOauthService.getAccessTokenByCode(code);
			GithubOAuthDTO.GithubUserInfoRes user= githubOauthService.getGithubUserByAccessToken(accessToken);
			log.info("user={}",user);
			LoginResult result=githubOauthService.saveOrUpdateUser(user,accessToken);
			return ApiResponse.onSuccess(SuccessStatus.USER_GITHUB_LOGIN_OK, MemberConverter.memberTo(result.member(),result.isNewUser(),result.accessToken()));
		} catch (NullPointerException e) {
			throw new AuthException(ErrorStatus.GITHUB_AUTH_ERROR);
		}
	}

	@PostMapping("/login/github_url")
	@Operation(summary = "깃허브 로그인 url 요청")
	public ApiResponse<MemberResponseDTO.Oauth2ResponseDTO> githubLogin(@RequestParam("redirect_uri") String redirectUri) {
		String url = githubOauthService.getGithubRedirectUrl(redirectUri);
		return ApiResponse.onSuccess(SuccessStatus.USER_GITHUB_LOGIN_OK, new MemberResponseDTO.Oauth2ResponseDTO(url));
	}

}
