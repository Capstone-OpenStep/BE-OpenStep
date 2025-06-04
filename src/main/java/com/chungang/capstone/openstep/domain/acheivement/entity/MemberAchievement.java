package com.chungang.capstone.openstep.domain.acheivement.entity;

import java.time.LocalDateTime;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.acheivement.enums.AcheivementType;
import com.chungang.capstone.openstep.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_achievement", uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "type"}))
public class MemberAchievement extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AcheivementType type;

	@Column(nullable = false)
	private int currentProgress = 0;

	@Column(nullable = false)
	private boolean unlocked=false;

	private LocalDateTime unlockedAt;
	private LocalDateTime lastProgressAt;

	@Builder
	private MemberAchievement(Member member, AcheivementType type) {
		this.member = member;
		this.type = type;
		member.getAchievements().add(this);// 양방향 연관관계 설정
	}


	public static MemberAchievement create(Member member, AcheivementType type) {
		MemberAchievement achievement = new MemberAchievement();
		achievement.member = member;
		achievement.type = type;
		return achievement;
	}

	public void incrementProgress(){
		if(this.unlocked) {
			return; // 이미 달성된 경우 진행하지 않음
		}

		this.currentProgress++;
		this.lastProgressAt = LocalDateTime.now();

		if(isTargetReached()){
			unlock();
		}
	}

	public void unlock() {
		this.unlocked = true;
		this.unlockedAt = LocalDateTime.now();
		this.currentProgress= this.type.getTargetCount();
	}


	public boolean isTargetReached(){
		return this.currentProgress >= this.type.getTargetCount();
	}

	public boolean isProgressRecent(int days){
		if(lastProgressAt==null) return false;
		return lastProgressAt.isAfter(LocalDateTime.now().minusDays(days));
	}

	public Long getMemberId() {
		return member.getMemberId();
	}

}
