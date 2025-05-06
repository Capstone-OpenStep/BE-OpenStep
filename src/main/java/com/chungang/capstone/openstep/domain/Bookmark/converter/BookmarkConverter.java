package com.chungang.capstone.openstep.domain.Bookmark.converter;

import com.chungang.capstone.openstep.domain.Bookmark.dto.BookmarkResponseDTO;
import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;

import java.time.LocalDateTime;

public class BookmarkConverter {

    // 북마크 추가
    public static BookmarkResponseDTO.CreateBookmarkResultDTO toCreateBookmarkResultDTO(Bookmark bookmark) {
        return BookmarkResponseDTO.CreateBookmarkResultDTO.builder()
                .bookmarkId(bookmark.getBookmarkId())
                .memberId(bookmark.getMember().getMemberId())
                .repoId(bookmark.getRepo().getRepoId())
                .createdAt(LocalDateTime.now())
                .build();
    }
    // Service
    public static Bookmark toBookmark(Member member, Repo repo) {
        return Bookmark.builder()
                .member(member)
                .repo(repo)
                .build();
    }

    // 북마크 삭제
    public static BookmarkResponseDTO.DeleteBookmarkResultDTO toDeleteBookmarkResultDTO(Long bookmarkId) {
        return BookmarkResponseDTO.DeleteBookmarkResultDTO.builder()
                .bookmarkId(bookmarkId)
                .build();
    }


}
