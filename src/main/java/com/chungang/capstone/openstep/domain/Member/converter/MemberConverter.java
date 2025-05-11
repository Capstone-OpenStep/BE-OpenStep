package com.chungang.capstone.openstep.domain.Member.converter;

import java.util.List;

import com.chungang.capstone.openstep.domain.Member.dto.MemberRequestDTO;
import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.common.InterestDomain;
import com.chungang.capstone.openstep.domain.common.InterestLanguage;

public class MemberConverter {
	public static MemberResponseDTO.MemberRes memberTo(Member member,boolean isNewMember,String accessToken) {
		return MemberResponseDTO.MemberRes.builder()
			.id(member.getMemberId())
			.githubId(member.getGithubId())
			.isNewMember(isNewMember)
			.accessToken(accessToken)
			.build();
	}

	public static MemberResponseDTO.DomainsRes toDomainRes(List<InterestDomain> domains) {
		return MemberResponseDTO.DomainsRes.builder()
				.domains(domains.stream().map(Enum::name).toList())
				.build();
	}

	public static MemberResponseDTO.LanguagesRes toLanguageRes(List<InterestLanguage> languages) {
		return MemberResponseDTO.LanguagesRes.builder()
				.languages(languages.stream().map(Enum::name).toList())
				.build();
	}

	// 이메일 회원가입용
	public static Member toMember(MemberRequestDTO.MemberSignUpRequestDTO request, String password) {
		Member member = Member.builder()
				.email(request.email())
				.password(password)
				.nickname(request.nickname())
				.githubId(request.githubId())
				.build();
		return member;
	}

}
