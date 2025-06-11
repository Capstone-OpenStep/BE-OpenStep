package com.chungang.capstone.openstep.domain.Task.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
import com.chungang.capstone.openstep.domain.Rank.entity.TaskXpLog;
import com.chungang.capstone.openstep.domain.Rank.repository.TaskXpLogRepository;
import com.chungang.capstone.openstep.domain.Rank.service.RankCommandService;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;
import com.chungang.capstone.openstep.domain.Task.repository.TaskRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.TaskException;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.MemberHandler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskXpEventListener {
    
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final TaskXpLogRepository taskXpLogRepository;
    private final RankCommandService rankCommandService;

    @EventListener
    @Transactional
    public void handleTaskStatusChangedForXp(TaskStatusChangedForXpEvent event) {
        try {
            Task task = taskRepository.findById(event.getTaskId())
                .orElseThrow(() -> new TaskException(ErrorStatus.TASK_NOT_FOUND));
            
            Member member = memberRepository.findById(event.getMemberId())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

            handleXpGranting(member, task, event.getNewStatus());
            
        } catch (Exception e) {
            log.warn("Failed to grant XP for task: {}, member: {}", 
                event.getTaskId(), event.getMemberId(), e);
        }
    }

    private void handleXpGranting(Member member, Task task, TaskStatus newStatus) {
        // 중복 지급 방지
        if (taskXpLogRepository.existsByTaskAndStatus(task, newStatus)) return;

        boolean shouldGrant = true;
        if (newStatus == TaskStatus.PR) {
            LocalDateTime today = LocalDate.now().atStartOfDay();
            LocalDateTime tomorrow = today.plusDays(1);

            int prCountToday = taskXpLogRepository.countByMemberAndStatusAndGrantedAtBetween(
                    member, TaskStatus.PR, today, tomorrow);
            shouldGrant = prCountToday < 3;
        }

        // XP 지급
        if (shouldGrant) {
            rankCommandService.addXp(member, newStatus.getXp());
        }

        // 로그 기록
        TaskXpLog log = TaskXpLog.builder()
                .task(task)
                .member(member)
                .status(newStatus)
                .xpGranted(shouldGrant)
                .grantedAt(LocalDateTime.now())
                .build();

        taskXpLogRepository.save(log);
    }
}