package com.chungang.capstone.openstep.domain.Member.dto;

import lombok.Getter;

public class GithubOAuthDTO {

	public record GithubOAuthReq(
		String client_id,
		String client_secret,
		String code
	) {}

	public record GithubOauthRes(
		String access_token,
		String token_type,
		String scope
	) {}

	public record GithubUserInfoRes(
		String login
	){}
}
