package com.chungang.capstone.openstep.domain.Member.converter;

import java.util.List;

import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.domain.Member.entity.Member;

public class MemberConverter {
	public static MemberResponseDTO.MemberRes memberTo(Member member,boolean isNewMember,String accessToken) {
		return MemberResponseDTO.MemberRes.builder()
			.id(member.getMemberId())
			.githubId(member.getGithubId())
			.isNewMember(isNewMember)
			.accessToken(accessToken)
			.build();
	}

	public static MemberResponseDTO.DomainsRes toDomainRes(List<String> domains) {
		return MemberResponseDTO.DomainsRes.builder()
			.domains(domains)
			.build();
	}

	public static MemberResponseDTO.SkillsRes toSkillRes(List<String> skills) {
		return MemberResponseDTO.SkillsRes.builder()
			.skills(skills)
			.build();
	}
}
