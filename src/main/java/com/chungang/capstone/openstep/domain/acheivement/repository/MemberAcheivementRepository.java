package com.chungang.capstone.openstep.domain.acheivement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chungang.capstone.openstep.domain.acheivement.entity.MemberAchievement;
import com.chungang.capstone.openstep.domain.acheivement.enums.AcheivementType;

public interface MemberAcheivementRepository extends JpaRepository<MemberAchievement,Long> {

	@Query("SELECT ma FROM MemberAchievement ma WHERE ma.member.memberId = :memberId AND ma.type = :type")
	Optional<MemberAchievement> findByMemberAndType(
		Long memberId,
		AcheivementType type
	);

	@Query("SELECT ma FROM MemberAchievement ma WHERE ma.member.memberId = :memberId")
	List<MemberAchievement> findByMemberId(Long memberId);

	@Query("SELECT ma FROM MemberAchievement ma WHERE ma.member.memberId = :memberId AND ma.unlocked = true")
	List<MemberAchievement> findUnlockedAchievementsByMemberId(Long memberId);

	@Query("SELECT ma FROM MemberAchievement ma WHERE ma.member.memberId IN :memberIds")
	List<MemberAchievement> findByUserIds(List<Long> memberIds);
}
