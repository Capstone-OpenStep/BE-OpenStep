package com.chungang.capstone.openstep.domain.Bookmark.converter;

import com.chungang.capstone.openstep.domain.Bookmark.dto.BookmarkResponseDTO;
import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public static BookmarkResponseDTO.BookmarkPreviewDTO toBookmarkPreviewDTO(Bookmark bookmark) {
        return BookmarkResponseDTO.BookmarkPreviewDTO.builder()
                .memberId(bookmark.getMember().getMemberId())
                .bookmarkId(bookmark.getBookmarkId())
                .repoId(bookmark.getRepo().getRepoId())
                .repoName(bookmark.getRepo().getRepoName())
                .language(bookmark.getRepo().getLanguage())
                .stars(bookmark.getRepo().getStars())
                .forks(bookmark.getRepo().getForks())
                .githubUrl(bookmark.getRepo().getGithubUrl())
                .isBookmarked(true)
                .createdAt(bookmark.getCreatedAt())
                .build();
    }

    public static BookmarkResponseDTO.BookmarkPreviewListDTO toBookmarkPreviewListDTO(List<Bookmark> bookmarkList) {
        List<BookmarkResponseDTO.BookmarkPreviewDTO> bookmarkPreviewDTOList = IntStream.range(0, bookmarkList.size())
                .mapToObj(i -> toBookmarkPreviewDTO(bookmarkList.get(i)))
                .collect(Collectors.toList());
        return BookmarkResponseDTO.BookmarkPreviewListDTO.builder()
                .bookmarkList(bookmarkPreviewDTOList)
                .build();
    }


}
