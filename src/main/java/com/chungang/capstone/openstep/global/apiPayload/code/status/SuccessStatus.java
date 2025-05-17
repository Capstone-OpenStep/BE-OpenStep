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
    MEMBER_CREATE_OK(HttpStatus.OK, "MEMBER_1000", "멤버 생성에 성공하였습니다."),
    MEMBER_GET_INTEREST_DOMAINS_OK(HttpStatus.OK, "MEMBER_1001", "유저 관심분야 조회에 성공하였습니다."),
    MEMBER_GET_INTEREST_LANGUAGES_OK(HttpStatus.OK, "MEMBER_1002", "유저 관심언어 조회에 성공하였습니다."),
    MEMBER_SELECT_INTEREST_DOMAINS_OK(HttpStatus.OK, "MEMBER_1003", "유저 관심분야 선택을 성공적으로 완료하였습니다."),
    MEMBER_SELECT_INTEREST_LANGUAGES_OK(HttpStatus.OK, "MEMBER_1004", "유저 관심언어 선택을 성공적으로 완료하였습니다."),
    MEMBER_SIGN_UP_OK(HttpStatus.OK, "MEMBER_1005", "유저 회원가입에 성공하였습니다."),
    MEMBER_LOGIN_OK(HttpStatus.OK, "MEMBER_1006", "유저 로그인에 성공하였습니다."),
    MEMBER_LOGOUT_OK(HttpStatus.OK, "MEMBER_1007", "유저 로그아웃에 성공하였습니다."),
    MEMBER_UPDATE_ACCESS_TOKEN_OK(HttpStatus.OK, "MEMBER_1008", "유저 액세스 토큰 재발급에 성공하였습니다."),

    // 레포지토리 관련 응답
    REPO_GET_TRENDING_OK(HttpStatus.OK, "REPO_2001", "트렌딩 레포지토리 리스트를 성공적으로 조회하였습니다."),
    REPO_GET_DETAIL_OK(HttpStatus.OK, "REPO_2002", "레포지토리 상세 정보를 성공적으로 조회하였습니다."),
    REPO_SUMMARY_OK(HttpStatus.OK, "REPO_2003", "레포지토리 요약 정보를 성공적으로 조회하였습니다."),
    REPO_ADD_BOOKMARK_OK(HttpStatus.OK, "REPO_2004", "레포지토리 북마크 추가가 완료되었습니다."),
    REPO_DELETE_BOOKMARK_OK(HttpStatus.OK, "REPO_2005", "레포지토리 북마크 삭제가 완료되었습니다."),
    REPO_BOOKMARK_LIST_OK(HttpStatus.OK, "REPO_2006", "레포지토리 북마크 리스트를 성공적으로 조회하였습니다."),
    REPO_GET_LIST_BY_NAME_OK(HttpStatus.OK, "REPO_2007", "레포지토리 이름으로 검색한 결과 리스트를 성공적으로 조회하였습니다."),

    // 이슈 관련 응답
    ISSUE_GET_TRENDING_OK(HttpStatus.OK, "ISSUE_3001", "트렌딩 이슈 리스트를 성공적으로 조회하였습니다."),
    ISSUE_GET_DETAIL_OK(HttpStatus.OK, "ISSUE_3002", "이슈 상세 정보를 성공적으로 조회하였습니다."),
    ISSUE_SUMMARY_OK(HttpStatus.OK, "ISSUE_3003", "이슈 요약 정보를 성공적으로 조회하였습니다."),

    //테스크 관련 응답
    TASK_ASSIGN_OK(HttpStatus.OK, "TASK_4001", "테스크 할당이 완료되었습니다."),
    TASK_GET_OK(HttpStatus.OK,"TASK_4002","테스크 상세조회가 완료되었습니다." );

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