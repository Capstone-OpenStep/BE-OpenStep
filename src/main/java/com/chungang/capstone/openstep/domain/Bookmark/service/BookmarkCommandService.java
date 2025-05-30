package com.chungang.capstone.openstep.domain.Bookmark.service;

import com.chungang.capstone.openstep.domain.Bookmark.converter.BookmarkConverter;
import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import com.chungang.capstone.openstep.domain.Bookmark.repository.BookmarkRepository;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Issue.repository.IssueRepository;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
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
    private final IssueRepository issueRepository;

    public Bookmark createBookmark(Long memberId, Long issueId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new GeneralException(ErrorStatus.ISSUE_NOT_FOUND));
        // 중복 북마크 방지
        if (bookmarkRepository.existsByMemberAndIssue(member, issue)) {
            throw new GeneralException(ErrorStatus.ISSUE_BOOKMARK_DUPLICATE);
        }
        Bookmark bookmark = BookmarkConverter.toBookmark(member, issue);
        return bookmarkRepository.save(bookmark);
    }

    // 북마크 삭제
    public void deleteBookmark(Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElseThrow(() -> new GeneralException(ErrorStatus.ISSUE_BOOKMARK_NOT_FOUND));
        bookmarkRepository.delete(bookmark);
    }



}
