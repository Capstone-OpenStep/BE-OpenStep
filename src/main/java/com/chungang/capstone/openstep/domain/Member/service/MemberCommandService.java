package com.chungang.capstone.openstep.domain.Member.service;

import com.chungang.capstone.openstep.domain.Member.entity.*;
import com.chungang.capstone.openstep.domain.common.InterestDomain;
import com.chungang.capstone.openstep.domain.common.InterestLanguage;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chungang.capstone.openstep.domain.Member.converter.MemberConverter;
import com.chungang.capstone.openstep.domain.Member.dto.MemberRequestDTO;
import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.domain.Member.repository.MemberDomainRepository;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
import com.chungang.capstone.openstep.domain.Member.repository.MemberLanguageRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService {

	private final MemberDomainRepository memberDomainRepository;
	private final MemberRepository memberRepository;
	private final MemberLanguageRepository memberLanguageRepository;


	public MemberResponseDTO.DomainsRes selectInterestDomains(Long memberId, MemberRequestDTO.UpdateDomainsReq domainsReq) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("Member not found"));
		memberDomainRepository.deleteAllByMember_MemberId(memberId);

		domainsReq.domains().forEach(domainName -> {
			InterestDomain domainEnum;
			try {
				domainEnum = InterestDomain.valueOf(domainName.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("존재하지 않는 도메인: " + domainName);
			}
			memberDomainRepository.save(
					MemberDomain.builder()
							.member(member)
							.domain(domainEnum)
							.build()
			);
		});
		return MemberConverter.toDomainRes(memberDomainRepository.findDomainsByMemberId(memberId));
	}



	public MemberResponseDTO.LanguagesRes selectInterestLanguages(Long memberId, MemberRequestDTO.UpdateLanguagesReq languagesReq) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("Member not found"));
		memberLanguageRepository.deleteAllByMember_MemberId(memberId);

		languagesReq.languages().forEach(languageName -> {
			InterestLanguage languageEnum;
			try {
				languageEnum = InterestLanguage.fromLabel(languageName);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("존재하지 않는 언어: " + languageName);
			}
			memberLanguageRepository.save(
					MemberLanguage.builder()
							.member(member)
							.language(languageEnum)
							.build()
			);
		});
		return MemberConverter.toLanguageRes(memberLanguageRepository.findLanguagesByMemberId(memberId));
	}




}
