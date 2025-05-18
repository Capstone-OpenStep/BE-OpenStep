package com.chungang.capstone.openstep.global.apiPayload.code.status;

import com.chungang.capstone.openstep.global.apiPayload.code.BaseErrorCode;
import com.chungang.capstone.openstep.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //GITHUB GRAPHQL 관련
    GITHUB_GRAPHQL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GRAPHQL_001", "서버측에서 깃허브 GraphQL API 호출에 실패했습니다."),

    //GITHUB REST 관련
    GITHUB_REST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "REST_001", "서버측에서 깃허브 REST API 호출에 실패했습니다."),

    // Auth 관련
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "토큰이 만료되었습니다."),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "토큰이 유효하지 않습니다."),
    INVALID_LOGIN_REQUEST(HttpStatus.UNAUTHORIZED, "AUTH_003", "올바른 아이디나 패스워드가 아닙니다."),
    MEMBER_WRONG_EMAIL(HttpStatus.UNAUTHORIZED, "AUTH_004", "이메일이 잘못되었습니다."),
    MEMBER_WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH_005", "비밀번호가 잘못되었습니다."),
    MEMBER_EMAIL_ALREADY_EXISTS(HttpStatus.UNAUTHORIZED, "AUTH_006", "이미 존재하는 이메일입니다."),
    MEMBER_NICKNAME_ALREADY_EXISTS(HttpStatus.UNAUTHORIZED, "AUTH_007", "이미 존재하는 닉네임입니다."),
    MEMBER_LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "AUTH_008", "로그인에 실패했습니다."),
    MEMBER_ALREADY_LOGGED_OUT(HttpStatus.UNAUTHORIZED, "AUTH_009", "이미 로그아웃된 계정입니다."),
    MEMBER_WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_010", "잘못된 토큰입니다."),

    //GITHUB AUTH 관련
    GITHUB_AUTH_ERROR(HttpStatus.UNAUTHORIZED, "AUTH_004", "깃허브 인증에 실패했습니다."),

    //access token 관련
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_005", "Access Token이 없습니다."),

    // 회원 관련 에러 1000
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_1001", "사용자가 없습니다."),
    MEMBER_NAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER_1002", "이름입력은 필수 입니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER_1003", "이미 존재하는 유저입니다."),

    // 레포지토리 관련 에러 2000
    REPO_NOT_FOUND(HttpStatus.NOT_FOUND, "REPO_2001", "존재하지 않는 레포지토리입니다."),
    REPO_BOOKMARK_DUPLICATE(HttpStatus.CONFLICT, "REPO_2002", "이미 북마크한 레포지토리입니다."),
    REPO_BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "REPO_2003", "북마크한 레포지토리가 아닙니다."),
    REPO_NO_INTEREST_INFO(HttpStatus.NOT_FOUND, "REPO_2004", "관심 레포지토리 정보가 없습니다."),

    // 이슈 관련 에러 3000
    ISSUE_NOT_FOUND(HttpStatus.NOT_FOUND, "ISSUE_3001", "존재하지 않는 이슈입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}

