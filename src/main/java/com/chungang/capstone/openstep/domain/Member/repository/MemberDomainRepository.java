package com.chungang.capstone.openstep.domain.Member.repository;

import java.util.List;

import com.chungang.capstone.openstep.domain.common.InterestDomain;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.chungang.capstone.openstep.domain.Member.entity.MemberDomain;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface MemberDomainRepository extends JpaRepository<MemberDomain, Long> {

//	@Modifying
//	@Query("DELETE FROM MemberDomain md WHERE md.member.memberId = :memberId")
//	void deleteAllByMemberId(@Param("memberId") Long memberId);
	void deleteAllByMember_MemberId(Long memberId);

	@Query("SELECT md.domain FROM MemberDomain md WHERE md.member.memberId = :memberId")
	List<InterestDomain> findDomainsByMemberId(@Param("memberId") Long memberId);
}
