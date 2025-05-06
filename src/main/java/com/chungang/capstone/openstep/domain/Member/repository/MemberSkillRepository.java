package com.chungang.capstone.openstep.domain.Member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.chungang.capstone.openstep.domain.Member.entity.MemberSkill;
import com.chungang.capstone.openstep.domain.Member.entity.Skill;

@Repository
public interface MemberSkillRepository extends JpaRepository<MemberSkill,Long> {

	@Query(nativeQuery = true,
		value = "SELECT s.name FROM member_skill ms " +
			"JOIN skill s ON ms.skill_id = s.id " +
			"WHERE ms.member_id = :memberId")
	List<String> findSkillsByMemberId(Long memberId);

	@Modifying(clearAutomatically = true)
	@Query(nativeQuery = true,
		value = "DELETE FROM member_skill WHERE member_id = :memberId")
	void deleteAllByMemberId(Long memberId);
}
