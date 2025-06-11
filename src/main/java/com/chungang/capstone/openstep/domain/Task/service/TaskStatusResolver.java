package com.chungang.capstone.openstep.domain.Task.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.dto.GithubTaskInfo;
import com.chungang.capstone.openstep.domain.Github.dto.PullRequestResponse;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskStatusResolver {
	public TaskStatus resolveStatus(Task task, GithubTaskInfo githubInfo) {
		log.debug("Resolving status for task: {}, githubInfo: {}",
			task.getTaskId(), githubInfo);

		// Fork가 없는 경우
		if (!githubInfo.isForkExist()) {
			return TaskStatus.NOT_STARTED;
		}

		// PR이 없는 경우
		if (!githubInfo.hasPullRequest()) {
			// 기존에 진행 중이었다면 유지, 아니면 FORKED
			return task.getStatus() == TaskStatus.PROGRESS
				? TaskStatus.PROGRESS : TaskStatus.FORKED;
		}

		// PR이 있는 경우 상태별 판단
		if (githubInfo.isPullRequestMerged()) {
			return TaskStatus.MERGED;
		} else if (githubInfo.isPullRequestClosed()) {
			return TaskStatus.REJECTED;
		} else if (githubInfo.isHasReview()) {
			return TaskStatus.REVIEW;
		} else {
			return TaskStatus.PR;
		}
	}
}
