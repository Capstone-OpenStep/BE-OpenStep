package com.chungang.capstone.openstep.domain.Task.event;

import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

// XP 부여 전용 이벤트
@Getter
@AllArgsConstructor
public class TaskStatusChangedForXpEvent {
    private final Long memberId;
    private final Long taskId;
    private final TaskStatus newStatus;
}