package com.chungang.capstone.openstep.domain.Member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chungang.capstone.openstep.domain.Member.converter.MemberConverter;
import com.chungang.capstone.openstep.domain.Member.dto.GithubOAuthDTO;
import com.chungang.capstone.openstep.domain.Member.dto.LoginResult;
import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
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
	public ApiResponse<MemberResponseDTO.MemberResponse> getUserByGithub(@RequestParam("code") String code){
		try {
			String accessToken = githubOauthService.getAccessTokenByCode(code);
			GithubOAuthDTO.GithubUserInfoRes user= githubOauthService.getGithubUserByAccessToken(accessToken);
			log.info("user={}",user);
			LoginResult result=githubOauthService.saveOrUpdateUser(user);
			return ApiResponse.onSuccess(SuccessStatus.USER_GITHUB_LOGIN_OK, MemberConverter.memberTo(result.member(),result.isNewUser(),result.accessToken()));
		} catch (NullPointerException e) {
			throw new AuthException(ErrorStatus.GITHUB_AUTH_ERROR);
		}
	}

}
