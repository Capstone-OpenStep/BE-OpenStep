package com.chungang.capstone.openstep.global.apiPayload.code.status;

import com.chungang.capstone.openstep.global.apiPayload.code.BaseCode;
import com.chungang.capstone.openstep.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 보안
    USER_EMAIL_LOGIN_OK(HttpStatus.OK, "AUTH2001", "회원 이메일 로그인이 완료되었습니다."),
    USER_GITHUB_LOGIN_OK(HttpStatus.OK, "AUTH2002", "회원 깃허브 로그인이 완료되었습니다."),
    USER_DELETE_OK(HttpStatus.OK, "AUTH2004", "회원 탈퇴가 완료되었습니다."),
    USER_REFRESH_OK(HttpStatus.OK, "AUTH2005", "토큰 재발급이 완료되었습니다."),
    USER_REGISTER_OK(HttpStatus.OK, "AUTH2000", "회원 가입이 완료되었습니다."),

    // 유저 관련 응답
    MEMBER_OK(HttpStatus.OK, "MEMBER_1000", "성공입니다."),

    // 레포지토리 관련 응답
    REPO_GET_TRENDING_OK(HttpStatus.OK, "REPO_2001", "트렌딩 레포지토리 리스트를 성공적으로 조회하였습니다."),
    REPO_GET_DETAIL_OK(HttpStatus.OK, "REPO_2002", "레포지토리 상세 정보를 성공적으로 조회하였습니다."),
    REPO_SUMMARY_OK(HttpStatus.OK, "REPO_2003", "레포지토리 요약 정보를 성공적으로 조회하였습니다."),
    REPO_ADD_BOOKMARK_OK(HttpStatus.OK, "REPO_2004", "레포지토리 북마크 추가가 완료되었습니다."),
    REPO_DELETE_BOOKMARK_OK(HttpStatus.OK, "REPO_2005", "레포지토리 북마크 삭제가 완료되었습니다."),

    // 이슈 관련 응답
    ISSUE_GET_TRENDING_OK(HttpStatus.OK, "ISSUE_3001", "트렌딩 이슈 리스트를 성공적으로 조회하였습니다."),
    ISSUE_GET_DETAIL_OK(HttpStatus.OK, "ISSUE_3002", "이슈 상세 정보를 성공적으로 조회하였습니다."),
    ISSUE_SUMMARY_OK(HttpStatus.OK, "ISSUE_3003", "이슈 요약 정보를 성공적으로 조회하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}