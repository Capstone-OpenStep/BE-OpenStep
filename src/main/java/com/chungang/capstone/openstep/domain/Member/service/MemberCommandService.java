package com.chungang.capstone.openstep.domain.Member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chungang.capstone.openstep.domain.Member.converter.MemberConverter;
import com.chungang.capstone.openstep.domain.Member.dto.MemberRequestDTO;
import com.chungang.capstone.openstep.domain.Member.dto.MemberResponseDTO;
import com.chungang.capstone.openstep.domain.Member.entity.Domain;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.entity.MemberDomain;
import com.chungang.capstone.openstep.domain.Member.entity.MemberSkill;
import com.chungang.capstone.openstep.domain.Member.entity.Skill;
import com.chungang.capstone.openstep.domain.Member.repository.DomainRepository;
import com.chungang.capstone.openstep.domain.Member.repository.MemberDomainRepository;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
import com.chungang.capstone.openstep.domain.Member.repository.MemberSkillRepository;
import com.chungang.capstone.openstep.domain.Member.repository.SkillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService {

	private final MemberDomainRepository memberDomainRepository;
	private final MemberRepository memberRepository;
	private final DomainRepository domainRepository;
	private final MemberSkillRepository memberSkillRepository;
	private final SkillRepository skillRepository;

	public MemberResponseDTO.DomainsRes updateDomains(Long memberId,
		MemberRequestDTO.UpdateDomainsReq domainsReq) {
		memberDomainRepository.deleteAllByMemberId(memberId);
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("Member not found"));
		domainsReq.domains().forEach(domainName -> {
			Domain domain = domainRepository.findByName(domainName);
			if (domain == null) {
				throw new IllegalArgumentException("Domain not found");
			}
			memberDomainRepository.save(
				MemberDomain.builder()
					.member(member)
					.domain(domain)
					.build()
			);
		});

		return MemberConverter.toDomainRes(memberDomainRepository.findDomainsByMemberId(memberId));

	}

	public MemberResponseDTO.SkillsRes updateSkills(Long memberId, MemberRequestDTO.UpdateSkillsReq skillsReq) {
		memberSkillRepository.deleteAllByMemberId(memberId);
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("Member not found"));
		skillsReq.skills().forEach(skillName -> {
			Skill skill = skillRepository.findByName(skillName);
			if (skill == null) {
				throw new IllegalArgumentException("Skill not found");
			}
			memberSkillRepository.save(
				MemberSkill.builder()
					.member(member)
					.skill(skill)
					.build()
			);
		});

		return MemberConverter.toSkillRes(memberSkillRepository.findSkillsByMemberId(memberId));
	}
}
