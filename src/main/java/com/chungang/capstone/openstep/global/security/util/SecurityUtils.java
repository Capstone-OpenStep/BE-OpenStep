package com.chungang.capstone.openstep.global.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.global.security.principal.PrincipalDetails;

public class SecurityUtils {
	public static Long getCurrentMemberId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == "anonymousUser") {
			throw new IllegalStateException("인증된 사용자 정보가 없습니다.");
		}

		PrincipalDetails principal = (PrincipalDetails) auth.getPrincipal();
		return Long.parseLong(principal.getUsername());
	}
	public static String getCurrentMemberGithubId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == "anonymousUser") {
			throw new IllegalStateException("인증된 사용자 정보가 없습니다.");
		}

		PrincipalDetails principal = (PrincipalDetails) auth.getPrincipal();
		return principal.getGithubId();
	}
	public static Member getCurrentMember() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == "anonymousUser") {
			throw new IllegalStateException("인증된 사용자 정보가 없습니다.");
		}

		PrincipalDetails principal = (PrincipalDetails) auth.getPrincipal();
		return principal.getMember();
	}

	public static Member getCurrentMemberOrNull() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
			return null;
		}

		try {
			PrincipalDetails principal = (PrincipalDetails) auth.getPrincipal();
			return principal.getMember();
		} catch (ClassCastException e) {
			return null;
		}
	}

}
