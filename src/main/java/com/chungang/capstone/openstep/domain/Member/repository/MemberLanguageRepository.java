package com.chungang.capstone.openstep.domain.Member.repository;

import java.util.List;

import com.chungang.capstone.openstep.domain.common.InterestLanguage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chungang.capstone.openstep.domain.Member.entity.MemberLanguage;

@Repository
public interface MemberLanguageRepository extends JpaRepository<MemberLanguage,Long> {

//	@Modifying(clearAutomatically = true)
//	@Transactional
//	@Query("DELETE FROM MemberLanguage ml WHERE ml.member.memberId = :memberId")
//	void deleteAllByMemberId(@Param("memberId") Long memberId);
//
//	@Query("SELECT ml.language FROM MemberLanguage ml WHERE ml.member.memberId = :memberId")
//	List<InterestLanguage> findLanguagesByMemberId(@Param("memberId") Long memberId);

	void deleteAllByMember_MemberId(Long memberId);

	@Query("SELECT ml.language FROM MemberLanguage ml WHERE ml.member.memberId = :memberId")
	List<InterestLanguage> findLanguagesByMemberId(@Param("memberId") Long memberId);

}
