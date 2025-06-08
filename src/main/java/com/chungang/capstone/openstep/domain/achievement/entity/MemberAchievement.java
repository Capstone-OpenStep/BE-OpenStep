package com.chungang.capstone.openstep.domain.achievement.entity;

import java.time.LocalDateTime;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.achievement.enums.AchievementType;
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
@Table(name = "member_achievements", uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "type"}))
public class MemberAchievement extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AchievementType type;

	@Column(nullable = false)
	private int currentProgress = 0;

	@Column(nullable = false)
	private boolean unlocked=false;

	private LocalDateTime unlockedAt;
	private LocalDateTime lastProgressAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trigger_task_id")
	private Task triggerTask;  // 업적을 달성하게 한 Task

	@Column(name = "trigger_task_title")
	private String triggerTaskTitle;  // Task 제목 (스냅샷)

	@Column(name = "trigger_repo_name")
	private String triggerRepoName;   // Repo 이름 (스냅샷)

	@Builder
	private MemberAchievement(Member member, AchievementType type) {
		this.member = member;
		this.type = type;
		member.getMemberAchievements().add(this);// 양방향 연관관계 설정
	}


	public static MemberAchievement create(Member member, AchievementType type) {
		MemberAchievement memberAchievement = new MemberAchievement();
		memberAchievement.member = member;
		memberAchievement.type = type;
		return memberAchievement;
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

	public void resetProgress() {
		this.currentProgress = 0;
		this.lastProgressAt = null;
	}

	//업적 달성 시 트리거 Task 정보 저장
	public void unlockWithTrigger(Task triggerTask) {
		this.unlocked = true;
		this.unlockedAt = LocalDateTime.now();
		this.currentProgress = this.type.getTargetCount();

		// 트리거 Task 정보 저장
		if (triggerTask != null) {
			this.triggerTask = triggerTask;
			this.triggerTaskTitle = triggerTask.getIssue().getTitle();
			this.triggerRepoName = triggerTask.getIssue().getRepo().getRepoName();
		}
	}

	// 진행도 증가 시 최근 Task 정보 업데이트
	public void incrementProgressWithTrigger(Task triggerTask) {
		if (this.unlocked) return;

		this.currentProgress++;
		this.lastProgressAt = LocalDateTime.now();

		// 최근 작업한 Task 정보 업데이트
		if (triggerTask != null) {
			this.triggerTask = triggerTask;
			this.triggerTaskTitle = triggerTask.getIssue().getTitle();
			this.triggerRepoName = triggerTask.getIssue().getRepo().getRepoName();
		}

		if (isTargetReached()) {
			this.unlocked = true;
			this.unlockedAt = LocalDateTime.now();
		}
	}
}
