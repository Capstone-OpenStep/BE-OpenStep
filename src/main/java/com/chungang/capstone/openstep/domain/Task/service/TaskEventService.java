package com.chungang.capstone.openstep.domain.Task.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;
import com.chungang.capstone.openstep.domain.Task.event.TaskStatusChangedForXpEvent;
import com.chungang.capstone.openstep.domain.achievement.event.PrCreatedEvent;
import com.chungang.capstone.openstep.domain.achievement.event.TaskActivityEvent;
import com.chungang.capstone.openstep.domain.achievement.event.TaskCompletedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventService {

	private final ApplicationEventPublisher eventPublisher;

	//테스크 상태에 따라 이벤트를 발행해주는 서비스

	public void publishStatusChangeEvent(Member member, Task task, TaskStatus oldStatus, TaskStatus newStatus) {
		if(oldStatus == newStatus) {
			return;
		}

		try {
			log.info("Publishing events for task status change: {} -> {} for task: {}, user: {}",
				oldStatus, newStatus, task.getTaskId(), member.getMemberId());

			eventPublisher.publishEvent(new TaskActivityEvent(
				member.getMemberId(),
				task.getTaskId(),
				oldStatus,
				newStatus
			));

			eventPublisher.publishEvent(new TaskStatusChangedForXpEvent(
				member.getMemberId(), task.getTaskId(), newStatus));

			//pr 생성시
			if(newStatus == TaskStatus.PR) {
				eventPublisher.publishEvent(new PrCreatedEvent(
					member.getMemberId(),
					task.getTaskId(),
					task.getIssue().getTitle(),
					task.getIssue().getBody()
				));
			}

			//task 완료시
			if(newStatus == TaskStatus.MERGED || newStatus == TaskStatus.REJECTED) {
				eventPublisher.publishEvent(new TaskCompletedEvent(
					member.getMemberId(),
					task.getTaskId(),
					task.getIssue().getRepo().getRepoName()
				));
			}
		}catch (Exception e) {
			//업적 발행 실패시 로그만 남기고 넘어감
			log.warn("Failed to publish achievement event for task: {}, member: {}", task.getTaskId(), member.getMemberId(), e);
		}
	}

}
