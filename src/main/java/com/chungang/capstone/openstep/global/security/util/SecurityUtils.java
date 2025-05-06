package com.chungang.capstone.openstep.global.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
}
