package com.chungang.capstone.openstep.domain.Member.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.chungang.capstone.openstep.domain.Member.dto.GithubOAuthDTO;
import com.chungang.capstone.openstep.domain.Member.dto.LoginResult;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
import com.chungang.capstone.openstep.global.security.filter.JwtRequestFilter;
import com.chungang.capstone.openstep.global.security.provider.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GithubOauthService {

	@Value("${github.client-id}")
	private String clientId;

	@Value("${github.client-secret}")
	private String clientSecret;

	private final JwtTokenProvider jwtTokenProvider;

	private final RestTemplate restTemplate=new RestTemplate();

	private final MemberRepository memberRepository;

	public String getAccessTokenByCode(String code) {
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
	public GithubOAuthDTO.GithubUserInfoRes getGithubUserByAccessToken(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.set("Accept", "application/json");

		HttpEntity<Void> request=new HttpEntity<>(headers);
		//access token을 이용해 사용자 정보를 요청하는 API 호출

		return Objects.requireNonNull(restTemplate.exchange(
			"https://api.github.com/user",
			HttpMethod.GET,
			request,
			GithubOAuthDTO.GithubUserInfoRes.class
		).getBody());
	}

	public LoginResult saveOrUpdateUser(GithubOAuthDTO.GithubUserInfoRes githubUser) {
		// 1. loginId로 DB에서 회원 조회
		Member member = memberRepository.findByGithubId(githubUser.login());
		boolean isNewUser = false;
		// 2. 회원이 존재하지 않으면 회원가입
		if (member == null) {
			member = Member.builder()
				.githubId(githubUser.login())
				.build();
			memberRepository.save(member);
			isNewUser = true;
		}
		// 3. JWT 토큰 생성
		String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId());
		return LoginResult.builder()
			.member(member)
			.isNewUser(isNewUser)
			.accessToken(accessToken)
			.build();
	}
}
