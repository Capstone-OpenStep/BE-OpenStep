package com.chungang.capstone.openstep.domain.Bookmark.service;

import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import com.chungang.capstone.openstep.domain.Bookmark.repository.BookmarkRepository;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookmarkQueryService {
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;

    public Long findBookmarkIdByMemberAndIssue(Long memberId, Long issueId) {
        return bookmarkRepository.findBookmarkIdByMemberAndIssue(memberId, issueId);
    }

    public List<Bookmark> getBookmarkList(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        List<Bookmark> memberBookmarks = bookmarkRepository.findAllByMember(member);
        return memberBookmarks;
    }

}
