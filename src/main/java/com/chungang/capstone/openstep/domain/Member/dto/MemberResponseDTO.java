package com.chungang.capstone.openstep.domain.Member.dto;

import lombok.Builder;

public class MemberResponseDTO {
	@Builder
	public record MemberResponse(
		Long id,
		String githubId,
		Boolean isNewMember,
		String accessToken
	) { }
}
