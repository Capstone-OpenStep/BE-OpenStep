package com.chungang.capstone.openstep.domain.Member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.chungang.capstone.openstep.domain.Member.entity.MemberDomain;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface MemberDomainRepository extends JpaRepository<MemberDomain, Long> {

	@Query(nativeQuery = true,
		value = "SELECT d.name FROM member_domain md " +
			"JOIN domain d ON md.domain_id = d.id " +
			"WHERE md.member_id = :memberId")
	List<String> findDomainsByMemberId(Long memberId);


	@Modifying(clearAutomatically = true)
	@Query(nativeQuery = true,
		value = "DELETE FROM member_domain WHERE member_id = :memberId")
	void deleteAllByMemberId(@Param("memberId") Long memberId);
}
