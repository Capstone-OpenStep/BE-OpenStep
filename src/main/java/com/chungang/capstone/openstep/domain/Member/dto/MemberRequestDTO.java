package com.chungang.capstone.openstep.domain.Member.dto;

import com.chungang.capstone.openstep.global.apiPayload.exception.handler.MemberHandler;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.List;

public class MemberRequestDTO {

	// 회원가입 요청 DTO
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	@Builder
	public record MemberSignUpRequestDTO(
			String email,
			String password,
			String nickname,
			String githubId) {

		@JsonIgnore
		public Boolean isCorrect() {
			if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
				throw new MemberHandler(ErrorStatus.MEMBER_WRONG_EMAIL);
			}
			if (!password.matches("^[a-zA-Z0-9@$!%*#?&]{8,}$")) {
				throw new MemberHandler(ErrorStatus.MEMBER_WRONG_PASSWORD);
			}
			if (nickname.trim().isEmpty()) {
				throw new MemberHandler(ErrorStatus.MEMBER_NAME_NOT_EXIST);
			}
			return true;
		}
	}

	// 로그인 요청 DTO
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	@Builder
	public record MemberLoginRequestDTO(String email, String password) {
		public UsernamePasswordAuthenticationToken toAuthentication() {
			return new UsernamePasswordAuthenticationToken(email, password);
		}
	}

	// refresh token 요청 DTO
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	@Builder
	public record refreshRequestDTO(
			String refreshToken) {
	}

	public record UpdateDomainsReq(
//			@Schema(description = "도메인 리스트", example = "[\"BACKEND\", \"AI\"]", allowableValues = {
//					"FRONTEND", "BACKEND", "SPRING_BOOT", "REACT", "UI_UX", "DEVOPS", "CLOUD",
//					"DOCKER", "DATABASE", "MYSQL", "AI", "DEEP_LEARNING", "MOBILE", "SECURITY",
//					"EMBEDDED", "GAME_DEV", "BLOCKCHAIN", "DATA_SCIENCE", "LINUX", "GRAPHQL"
//			})
			List<String> domains
	) {}

	public record UpdateLanguagesReq(
//			@Schema(description = "언어 리스트", example = "[\"JAVA\", \"PYTHON\"]", allowableValues = {
//					"C", "CPP", "C_SHARP", "JAVA", "JAVASCRIPT", "TYPESCRIPT", "SWIFT", "KOTLIN",
//					"PYTHON", "RUST", "GO", "R", "RUBY", "PERL", "PHP", "SQL", "MATLAB", "SCRATCH"
//			})
			List<String> languages
	) {}



}
