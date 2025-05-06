package com.chungang.capstone.openstep.domain.Bookmark.repository;

import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 북마크 중복 확인
    boolean existsByMemberAndRepo(Member member, Repo repo);

    @Query("SELECT b.bookmarkId FROM Bookmark b WHERE b.member.memberId = :memberId AND b.repo.repoId = :repoId")
    Long findBookmarkIdByMemberAndRepo(@Param("memberId") Long memberId, @Param("repoId") Long repoId);
}
