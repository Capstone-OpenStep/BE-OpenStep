package com.chungang.capstone.openstep.domain.achievement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chungang.capstone.openstep.domain.achievement.entity.MemberAchievement;
import com.chungang.capstone.openstep.domain.achievement.enums.AchievementType;

public interface MemberAchievementRepository extends JpaRepository<MemberAchievement,Long> {


	@Query("SELECT ma FROM MemberAchievement ma WHERE ma.member.memberId = :memberId AND ma.type = :type")
	Optional<MemberAchievement> findByMemberIdAndType(
		Long memberId,
		AchievementType type
	);

	@Query("SELECT ma FROM MemberAchievement ma WHERE ma.member.memberId = :memberId")
	List<MemberAchievement> findByMemberId(Long memberId);

	@Query("SELECT ma FROM MemberAchievement ma WHERE ma.member.memberId = :memberId AND ma.unlocked = true")
	List<MemberAchievement> findUnlockedAchievementsByMemberId(Long memberId);

	@Query("SELECT ma FROM MemberAchievement ma WHERE ma.member.memberId IN :memberIds")
	List<MemberAchievement> findByMemberIds(List<Long> memberIds);

	@Query("SELECT COUNT(ma) FROM MemberAchievement ma WHERE ma.member.memberId = :memberId AND ma.unlocked = true")
	int countUnlockedByMemberId(
		Long memberId
	);
}
