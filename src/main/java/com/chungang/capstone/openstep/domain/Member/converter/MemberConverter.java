package com.chungang.capstone.openstep.domain.Member.converter;

import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.domain.Member.entity.Member;

public class MemberConverter {
	public static MemberResponseDTO.MemberResponse memberTo(Member member,boolean isNewMember,String accessToken) {
		return MemberResponseDTO.MemberResponse.builder()
			.id(member.getMemberId())
			.githubId(member.getGithubId())
			.isNewMember(isNewMember)
			.accessToken(accessToken)
			.build();
	}
}
