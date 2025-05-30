package com.chungang.capstone.openstep.domain.Bookmark.controller;

import com.chungang.capstone.openstep.domain.Bookmark.dto.BookmarkResponseDTO;
import com.chungang.capstone.openstep.domain.Bookmark.converter.BookmarkConverter;
import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import com.chungang.capstone.openstep.domain.Bookmark.service.BookmarkCommandService;
import com.chungang.capstone.openstep.domain.Bookmark.service.BookmarkQueryService;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
@Slf4j
@Tag(name = "이슈 북마크 API", description = "이슈 북마크 추가/삭제 관련 API입니다.")
public class BookmarkController {
    private final BookmarkCommandService bookmarkCommandService;
    private final BookmarkQueryService bookmarkQueryService;

    // 이슈 북마크 설정
    @PostMapping("/add/{member-id}/{repo-id}")
    @Operation(summary = "이슈 북마크 설정 API", description = "특정 오픈소스 이슈를 북마크합니다.")
    public ApiResponse<BookmarkResponseDTO.CreateBookmarkResultDTO> createBookmark(@PathVariable("member-id") Long memberId, @PathVariable("issue-id") Long issueId) {
        Bookmark bookmark = bookmarkCommandService.createBookmark(memberId, issueId);
        return ApiResponse.onSuccess(SuccessStatus.REPO_ADD_BOOKMARK_OK, BookmarkConverter.toCreateBookmarkResultDTO(bookmark));
    }

    // 이슈 북마크 삭제
    @DeleteMapping("/delete/{member-id}/{repo-id}")
    @Operation(summary = "이슈 북마크 삭제 API", description = "특정 오픈소스 이슈의 북마크를 해제합니다.")
    public ApiResponse<BookmarkResponseDTO.DeleteBookmarkResultDTO> deleteBookmark(@PathVariable("member-id") Long memberId, @PathVariable("issue-id") Long issueId) {
        Long bookmarkId = bookmarkQueryService.findBookmarkIdByMemberAndIssue(memberId, issueId);
        bookmarkCommandService.deleteBookmark(bookmarkId);
        return ApiResponse.onSuccess(SuccessStatus.REPO_DELETE_BOOKMARK_OK, BookmarkConverter.toDeleteBookmarkResultDTO(bookmarkId));
    }

    // 사용자가 북마크한 이슈 리스트 조회
    @GetMapping("/list/{member-id}")
    @Operation(summary = "사용자가 북마크한 이슈 리스트 조회 API", description = "특정 사용자가 북마크한 이슈 리스트를 조회합니다.")
    public ApiResponse<BookmarkResponseDTO.BookmarkPreviewListDTO> getBookmarkList(@PathVariable("member-id") Long memberId) {
        List<Bookmark> bookmarkList = bookmarkQueryService.getBookmarkList(memberId);
        return ApiResponse.onSuccess(SuccessStatus.REPO_BOOKMARK_LIST_OK, BookmarkConverter.toBookmarkPreviewListDTO(bookmarkList));
    }
}
