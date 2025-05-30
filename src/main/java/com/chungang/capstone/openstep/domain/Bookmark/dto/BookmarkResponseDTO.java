package com.chungang.capstone.openstep.domain.Bookmark.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
        Long issueId;
        String issueTitle;
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

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkPreviewDTO {
        private Long memberId;
        private Long bookmarkId;
        private Long issueId;
        private Long repoId;
        private String repoName;
        private String ownerName;
        private String issueTitle;
        private String language;
        private Integer stars;
        private String githubUrl;
        private boolean isBookmarked;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkPreviewListDTO {
        private List<BookmarkResponseDTO.BookmarkPreviewDTO> bookmarkList;
    }
}
