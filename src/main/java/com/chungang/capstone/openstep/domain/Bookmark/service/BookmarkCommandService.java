package com.chungang.capstone.openstep.domain.Bookmark.service;

import com.chungang.capstone.openstep.domain.Bookmark.converter.BookmarkConverter;
import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import com.chungang.capstone.openstep.domain.Bookmark.repository.BookmarkRepository;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Repo.repository.RepoRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookmarkCommandService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final RepoRepository repoRepository;

    public Bookmark createBookmark(Long memberId, Long repoId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        Repo repo = repoRepository.findById(repoId).orElseThrow(() -> new GeneralException(ErrorStatus.REPO_NOT_FOUND));
        // 중복 북마크 방지
        if (bookmarkRepository.existsByMemberAndRepo(member, repo)) {
            throw new GeneralException(ErrorStatus.REPO_BOOKMARK_DUPLICATE);
        }
        Bookmark bookmark = BookmarkConverter.toBookmark(member, repo);
        return bookmarkRepository.save(bookmark);
    }

    // 북마크 삭제
    public void deleteBookmark(Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElseThrow(() -> new GeneralException(ErrorStatus.REPO_BOOKMARK_NOT_FOUND));
        bookmarkRepository.delete(bookmark);
    }



}
