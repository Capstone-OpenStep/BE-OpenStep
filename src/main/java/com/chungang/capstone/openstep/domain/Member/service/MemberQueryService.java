package com.chungang.capstone.openstep.domain.Member.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chungang.capstone.openstep.domain.Member.converter.MemberConverter;
import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.domain.Member.repository.MemberDomainRepository;
import com.chungang.capstone.openstep.domain.Member.repository.MemberSkillRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberQueryService {
	private final MemberDomainRepository memberDomainRepository;
	private final MemberSkillRepository memberSkillRepository;

	public MemberResponseDTO.DomainsRes getDomains(Long memberId) {
		List<String> domains=memberDomainRepository.findDomainsByMemberId(memberId);
		return MemberConverter.toDomainRes(domains);
	}

	public MemberResponseDTO.SkillsRes getSkills(Long memberId) {
		List<String> skills=memberSkillRepository.findSkillsByMemberId(memberId);
		return MemberConverter.toSkillRes(skills);
	}
}
