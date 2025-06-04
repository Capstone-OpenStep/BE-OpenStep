package com.chungang.capstone.openstep.domain.Member.dto;

import java.util.List;

import com.chungang.capstone.openstep.global.security.provider.TokenInfo;
import com.fasterxml.classmate.AnnotationOverrides;

import lombok.Builder;

public class MemberResponseDTO {

	@Builder
	public record MemberRes(
		Long id,
		String githubId,
		Boolean isNewMember,
		String accessToken
	) { }

	@Builder
	public record DomainsRes(
		List<String> domains
	) {}

	@Builder
	public record LanguagesRes(
		List<String> languages
	) {}

	@Builder
	public record Oauth2ResponseDTO(String redirectUri) {}

	@Builder
	public record MemberTokenResponseDTO (
			TokenInfo tokenInfo,
			String email,
			String nickname,
			String githubId,
			Long memberId) {
	}

	@Builder
	public record TokenRefreshResponseDTO(
			String accessToken,
			String email,
			String nickname,
			String githubId,
			Long memberId) {}


	@Builder
	public record GitHubProfileDTO(
			String githubId,
			String email,
			String avatarUrl,
			String location,
			String profileUrl,
			int followersCount,
			int followingCount
	) {}


}
