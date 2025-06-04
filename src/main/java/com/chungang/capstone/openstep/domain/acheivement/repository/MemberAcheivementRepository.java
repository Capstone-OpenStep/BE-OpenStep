package com.chungang.capstone.openstep.domain.acheivement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chungang.capstone.openstep.domain.acheivement.entity.MemberAchievement;
import com.chungang.capstone.openstep.domain.acheivement.enums.AcheivementType;

public interface MemberAcheivementRepository extends JpaRepository<MemberAchievement,Long> {


	Optional<MemberAchievement> findByMemberIdAndType(
		Long memberId,
		AcheivementType type
	);

	List<MemberAchievement> findByMemberId(Long memberId);

	List<MemberAchievement> findUnlockedAchievementsByMemberId(Long memberId);

	@Query("SELECT ma FROM MemberAchievement ma WHERE ma.member.memberId IN :memberIds")
	List<MemberAchievement> findByMemberIds(List<Long> memberIds);
}
