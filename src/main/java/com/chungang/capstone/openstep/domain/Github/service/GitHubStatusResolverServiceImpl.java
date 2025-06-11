package com.chungang.capstone.openstep.domain.Github.service;

import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.dto.PullRequestResponse;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;
import com.chungang.capstone.openstep.domain.Task.service.TaskCommandService;
import com.chungang.capstone.openstep.domain.achievement.event.TaskActivityEvent;
import com.chungang.capstone.openstep.domain.achievement.event.PrCreatedEvent;
import com.chungang.capstone.openstep.domain.achievement.event.TaskCompletedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubStatusResolverServiceImpl implements GitHubStatusResolverService {

    private final GitHubRestService gitHubRestService;
    private final ApplicationEventPublisher eventPublisher; // 추가
    private final TaskCommandService taskCommandService; // 추가

    @Override
    public TaskStatus resolveStatus(Task task, Member member) {
        String owner = task.getIssue().getRepo().getOwnerName();
        String repo = task.getIssue().getRepo().getRepoName();
        String user = member.getGithubId();
        String githubToken = member.getGithubAccessToken();

        TaskStatus oldStatus = task.getStatus(); //기존 상태 저장

        // 사용자가 레포를 포크했는지 확인
        if (!gitHubRestService.doesForkExist(user, repo, githubToken)) {
            TaskStatus newStatus = TaskStatus.NOT_STARTED;
            publishEventIfChanged(member, task, oldStatus, newStatus);
            return newStatus;
        }

        // PR 생성 여부
        PullRequestResponse.PullRequestRes pr = gitHubRestService.findPullRequest(owner, repo, task.getBranchName(), user, githubToken);
        if (pr == null) {
            // pr null인 경우는 PR이 생성되지 않은 상태
            TaskStatus newStatus;
            if (task.getStatus() == TaskStatus.PROGRESS) {
                newStatus = TaskStatus.PROGRESS; // 작업 중인 상태
            }else{
                newStatus = TaskStatus.FORKED;
            }
            publishEventIfChanged(member, task, oldStatus, newStatus);
            return newStatus;
        }else{
            // PR이 존재하는 경우, PR URL 업데이트
            taskCommandService.updatePrUrl(task, pr);
        }

        // 머지 or 반려 여부
        TaskStatus newStatus;
        if (pr.mergedAt() != null) {
            newStatus = TaskStatus.MERGED;
        } else if (Objects.equals(pr.state(), "closed")) {
            newStatus = TaskStatus.REJECTED;
        } else if (gitHubRestService.hasReview(owner, repo, pr.number(), githubToken)) {
            newStatus = TaskStatus.REVIEW;
        } else {
            newStatus = TaskStatus.PR; // PR 생성만 된 상태
        }

        //상태 변경 시 이벤트 발행
        publishEventIfChanged(member, task, oldStatus, newStatus);
        
        return newStatus;
    }

    //새로 추가: 상태 변경 시 이벤트 발행 메서드
    private void publishEventIfChanged(Member member, Task task, TaskStatus oldStatus, TaskStatus newStatus) {
        if (oldStatus == newStatus) {
            return; // 상태 변경이 없으면 이벤트 발행하지 않음
        }

        try {
            log.info("Task status changed: {} -> {} for task: {}, user: {}", 
                oldStatus, newStatus, task.getTaskId(), member.getMemberId());

            // 모든 상태 변경에 대해 활동 이벤트 발행
            eventPublisher.publishEvent(new TaskActivityEvent(
                member.getMemberId(),
                task.getTaskId(),
                oldStatus,
                newStatus
            ));

            // PR 생성 시
            if (newStatus == TaskStatus.PR) {
                eventPublisher.publishEvent(new PrCreatedEvent(
                    member.getMemberId(),
                    task.getTaskId(),
                    task.getIssue().getTitle(),
                    task.getIssue().getBody()
                ));
            }

            // Task 완료 시
            if (newStatus == TaskStatus.MERGED) {
                eventPublisher.publishEvent(new TaskCompletedEvent(
                    member.getMemberId(),
                    task.getTaskId(),
                    task.getIssue().getRepo().getRepoName()
                ));
            }

        } catch (Exception e) {
            log.warn("Failed to publish achievement event for task: {}, member: {}", 
                task.getTaskId(), member.getMemberId(), e);
        }
    }
}