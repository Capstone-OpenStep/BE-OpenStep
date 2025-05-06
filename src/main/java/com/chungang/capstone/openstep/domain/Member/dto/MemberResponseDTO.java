package com.chungang.capstone.openstep.domain.Member.dto;

import java.util.List;

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
	public record SkillsRes(
		List<String> skills
	) {}
}
