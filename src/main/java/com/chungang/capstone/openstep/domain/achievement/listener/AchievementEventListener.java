package com.chungang.capstone.openstep.domain.achievement.listener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;
import com.chungang.capstone.openstep.domain.Task.repository.TaskRepository;
import com.chungang.capstone.openstep.domain.achievement.enums.AchievementType;
import com.chungang.capstone.openstep.domain.achievement.event.CommentAddedEvent;
import com.chungang.capstone.openstep.domain.achievement.event.PrCreatedEvent;
import com.chungang.capstone.openstep.domain.achievement.event.TaskActivityEvent;
import com.chungang.capstone.openstep.domain.achievement.event.TaskCompletedEvent;
import com.chungang.capstone.openstep.domain.achievement.service.AchievementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AchievementEventListener {

	private final AchievementService achievementService;
	private final TaskRepository taskRepository;

	@EventListener
	@Async("achievementExecutor")
	public void handlePrCreatedEvent(PrCreatedEvent event) {
		log.info("processing PR created event for member: {}, PR : {}", event.getMemberId(), event.getTaskId());

		try{
			//first commit 업적 체크
			checkFirstCommitAchievement(event);

			//bughunter 업적 체크
			if( event.isHasBugKeywords()) {
				achievementService.incrementProgress(event.getMemberId(), AchievementType.BUG_HUNTER);
				log.info("Bug Hunter achievement progress incremented for member: {}", event.getMemberId());
			}

			//PR 마스터 업적 체크
			achievementService.incrementProgress(event.getMemberId(), AchievementType.PR_MASTER);
		}catch (Exception e){

			// 예외 발생 시 로깅. 메인 기능에 영향주면 안되니께 업적시스템이
			log.error("Error processing PR created event for member: {}, PR : {}", event.getMemberId(), event.getTaskId(), e);
		}
	}

	@EventListener
	@Async("achievementExecutor")
	public void handleTaskCompleted(TaskCompletedEvent event){
		log.info("processing Task completed event for member: {}, task : {}", event.getMemberId(), event.getTaskId());

		try{
			//Explorer 업적 체크
			checkExplorerAchievements(event);

		}catch (Exception e){
			log.error("Error processing Task completed event for member: {}, task : {}", event.getMemberId(), event.getTaskId(), e);
		}
	}

	@EventListener
	@Async("achievementExecutor")
	public void handleTaskActivity(TaskActivityEvent event) {
		log.info("Processing task activity event for user: {}, task: {}", event.getMemberId(), event.getTaskId());

		try {
			//활동 기반 꾸준한 개발자 업적 체크
			checkConsistendDevAchievement(event);

		} catch (Exception e) {
			log.error("Error processing task activity event for user: {}", event.getMemberId(), e);
		}
	}

	@EventListener
	@Async("achievementExecutor")
	public void handleCommentAdded(CommentAddedEvent event){
		log.info("processing Comment added event for member: {}", event.getMemberId());

		try{
			//mentor 업적 체크
			if(event.isHasPositivewords()){
				achievementService.incrementProgress(event.getMemberId(), AchievementType.MENTOR);
				log.info("Mentor achievement progress incremented for member: {}", event.getMemberId());
			}
		}catch (Exception e){
			log.error("Error processing Comment added event for member: {}", event.getMemberId(), e);
		}
	}

	private void checkFirstCommitAchievement(PrCreatedEvent event) {
		// 1. 이미 달성했는지 확인
		boolean alreadyUnlocked = achievementService.hasUnlockedAchievement(
			event.getMemberId(),
			AchievementType.FIRST_COMMIT
		);

		if (alreadyUnlocked) {
			return; // 이미 달성했으면 스킵
		}

		// 2. PR 상태인 Task 개수 확인 (Repository 직접 사용)
		long prTaskCount = taskRepository.countByMemberIdAndStatus(
			event.getMemberId(),
			TaskStatus.PR
		);

		if (prTaskCount == 1) { // 첫 번째 PR이면
			achievementService.unlock(event.getMemberId(), AchievementType.FIRST_COMMIT);
			log.info("First Commit achievement unlocked for member: {}", event.getMemberId());
		}
	}

	private void checkConsistendDevAchievement(TaskActivityEvent event) {
		LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

		// 최근 7일간 활동이 있었던 날들 조회
		List<LocalDate> activityDays = taskRepository.findDistinctActivityDatesByMemberIdSince(
			event.getMemberId(),
			sevenDaysAgo
		);

		if (isConsecutiveWorkDays(new HashSet<>(activityDays), 7)) {
			achievementService.unlock(event.getMemberId(), AchievementType.CONSISTENT_DEV);
			log.info("Consistent Dev achievement unlocked for user: {}", event.getMemberId());
		}
	}

	private void checkExplorerAchievements(TaskCompletedEvent event) {
		List<TaskStatus> workStatuses = Arrays.asList(
			TaskStatus.PROGRESS,
			TaskStatus.MERGED,
			TaskStatus.PR,
			TaskStatus.REVIEW
		);

		// 이 repo에서 처음 작업하는지 확인
		boolean hasWorkedInThisRepoBefore = taskRepository.existsByMemberIdAndRepoNameAndStatuses(
			event.getMemberId(),
			event.getRepoName(),
			workStatuses
		);

		if (!hasWorkedInThisRepoBefore) {
			long uniqueRepoCount = taskRepository.countDistinctReposByMemberIdAndStatuses(
				event.getMemberId(),
				workStatuses
			);

			if (uniqueRepoCount >= 2) {
				achievementService.unlock(event.getMemberId(), AchievementType.EXPLORER_LV1);
			}
			if (uniqueRepoCount >= 5) {
				achievementService.unlock(event.getMemberId(), AchievementType.EXPLORER_LV2);
			}
			if (uniqueRepoCount >= 10) {
				achievementService.unlock(event.getMemberId(), AchievementType.EXPLORER_LV3);
			}
		}
	}
	private boolean isConsecutiveWorkDays(Set<LocalDate> workDays, int requiredDays) {
		if (workDays.size() < requiredDays) return false;

		List<LocalDate> sortedDays = workDays.stream()
			.sorted()
			.toList();

		int consecutiveCount = 1;
		for (int i = 1; i < sortedDays.size(); i++) {
			if (sortedDays.get(i).equals(sortedDays.get(i-1).plusDays(1))) {
				consecutiveCount++;
				if (consecutiveCount >= requiredDays) {
					return true;
				}
			} else {
				consecutiveCount = 1;
			}
		}

		return false;
	}
}
