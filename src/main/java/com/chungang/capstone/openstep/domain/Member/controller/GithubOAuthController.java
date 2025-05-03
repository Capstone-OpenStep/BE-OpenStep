package com.chungang.capstone.openstep.domain.Member.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.chungang.capstone.openstep.domain.Member.dto.GithubOAuthDTO;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/github/auth")
@Tag(name = "GithubOauth",description = "깃허브 로그인 관련 API")
@Slf4j
public class GithubOAuthController {

	@Value("${github.client-id}")
	private String clientId;

	@Value("${github.client-secret}")
	private String clientSecret;

	private final RestTemplate restTemplate=new RestTemplate();


	@GetMapping("/callback")
	public ApiResponse<String> getUserInfo(@RequestParam("code") String code){
		log.info("code: {}", code);
		String accessToken =getAccessToken(code);
		log.info("accessToken: {}", accessToken);
		String userName=getUserName(accessToken);
		log.info("userName: {}", userName);
		return ApiResponse.onSuccess(SuccessStatus.USER_GITHUB_LOGIN_OK, userName);
	}

	private String getAccessToken(String code) {
		//code를 이용해 access token을 요청하는 API 호출
		//post를 통해 access token을 요청
		GithubOAuthDTO.GithubOAuthReq githubOAuthReq = new GithubOAuthDTO.GithubOAuthReq(
			clientId,
			clientSecret,
			code
		);

		ResponseEntity<GithubOAuthDTO.GithubOauthRes> response = restTemplate.postForEntity(
			"https://github.com/login/oauth/access_token",
			githubOAuthReq,
			GithubOAuthDTO.GithubOauthRes.class
		);
		return Objects.requireNonNull(response.getBody()).access_token();
	}
	private String getUserName(String accessToken) {
		HttpHeaders headers=new HttpHeaders();
		headers.setBearerAuth(accessToken);
		HttpEntity<Void> request=new HttpEntity<>(headers);

		return Objects.requireNonNull(restTemplate.exchange(
			"https://api.github.com/user",
			HttpMethod.GET,
			request,
			GithubOAuthDTO.GithubUserInfoRes.class
		).getBody()).name();
	}
}
