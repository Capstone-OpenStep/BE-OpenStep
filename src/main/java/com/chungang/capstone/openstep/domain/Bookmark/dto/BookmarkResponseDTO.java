package com.chungang.capstone.openstep.domain.Bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class BookmarkResponseDTO {

    // 북마크 추가
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateBookmarkResultDTO {
        Long bookmarkId;
        Long memberId;
        Long repoId;
        LocalDateTime createdAt;
    }

    // 북마크 삭제
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteBookmarkResultDTO {
        Long bookmarkId;
    }
}
