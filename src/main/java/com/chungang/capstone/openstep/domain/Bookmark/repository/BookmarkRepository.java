package com.chungang.capstone.openstep.domain.Bookmark.repository;

import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 북마크 중복 확인
    boolean existsByMemberAndIssue(Member member, Issue issue);

    List<Bookmark> findAllByMember(Member member);

    @Query("SELECT b.bookmarkId FROM Bookmark b WHERE b.member.memberId = :memberId AND b.issue.issueId = :issueId")
    Long findBookmarkIdByMemberAndIssue(@Param("memberId") Long memberId, @Param("issueId") Long issueId);
}
