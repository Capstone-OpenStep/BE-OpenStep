package com.chungang.capstone.openstep.domain.Member.dto;

import java.util.List;

public class MemberRequestDTO {
	public record UpdateDomainsReq(
		List<String> domains
	) { }

	public record UpdateSkillsReq(
		List<String> skills
	){}
}
