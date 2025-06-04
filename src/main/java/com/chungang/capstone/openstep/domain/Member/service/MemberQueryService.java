package com.chungang.capstone.openstep.domain.Member.service;

import java.util.List;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.MemberHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chungang.capstone.openstep.domain.Member.converter.MemberConverter;
import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.domain.Member.repository.MemberDomainRepository;
import com.chungang.capstone.openstep.domain.Member.repository.MemberLanguageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberQueryService {
	private final MemberDomainRepository memberDomainRepository;
	private final MemberLanguageRepository memberLanguageRepository;
	private final MemberRepository memberRepository;

//	public MemberResponseDTO.DomainsRes getDomains(Long memberId) {
//		List<String> domains=memberDomainRepository.findDomainsByMemberId(memberId);
//		return MemberConverter.toDomainRes(domains);
//	}
//
//	public MemberResponseDTO.LanguagesRes getLanguages(Long memberId) {
//		List<String> languages= memberLanguageRepository.findLanguagesByMemberId(memberId);
//		return MemberConverter.toLanguageRes(languages);
//	}

	public MemberResponseDTO.DomainsRes getDomains(Long memberId) {
		return MemberConverter.toDomainRes(
				memberDomainRepository.findDomainsByMemberId(memberId));
	}

	public MemberResponseDTO.LanguagesRes getLanguages(Long memberId) {
		return MemberConverter.toLanguageRes(
				memberLanguageRepository.findLanguagesByMemberId(memberId));
	}

	public Member getMemberById(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberHandler(MEMBER_NOT_FOUND));
	}
}
