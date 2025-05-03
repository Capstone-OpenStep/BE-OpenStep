package com.chungang.capstone.openstep.domain.Member.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.service.annotation.GetExchange;

import com.chungang.capstone.openstep.domain.Member.dto.GithubOAuthDTO;
import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;
import com.chungang.capstone.openstep.global.security.principal.PrincipalDetailsService;

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
	public ApiResponse<String> getUserInfo(@RequestParam String code){
		log.info("callback 내부");
		String accessToken =getAccessToken(code);
		String userName=getUserName(accessToken);
		return ApiResponse.onSuccess(SuccessStatus.USER_GITHUB_LOGIN_OK, userName);
	}

	private String getAccessToken(String code) {
		//code를 이용해 access token을 요청하는 API 호출
		//post를 통해 access token을 요청
		//TODO: NullPointerException 발생시 예외처리
		return restTemplate.postForObject(
			"https://github.com/login/oauth/access_token",
			//access token을 요청하기 위한 body
			new GithubOAuthDTO.GithubOAuthReq(clientId,clientSecret,code)
			, GithubOAuthDTO.GithubOauthRes.class
		).access_token();
	}
	private String getUserName(String accessToken) {
		HttpHeaders headers=new HttpHeaders();
		headers.setBearerAuth(accessToken);
		HttpEntity<Void> request=new HttpEntity<>(headers);

		return restTemplate.getForObject(
			"https://api.github.com/user",
			GithubOAuthDTO.GithubUserInfoRes.class
		).name();
	}
}
