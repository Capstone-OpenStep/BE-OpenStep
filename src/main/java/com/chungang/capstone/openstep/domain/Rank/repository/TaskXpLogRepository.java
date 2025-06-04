package com.chungang.capstone.openstep.domain.Rank.repository;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Rank.entity.TaskXpLog;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TaskXpLogRepository extends JpaRepository<TaskXpLog, Long> {

    boolean existsByTaskAndStatus(Task task, TaskStatus status);

    int countByMemberAndStatusAndGrantedAtBetween(
            Member member,
            TaskStatus status,
            LocalDateTime start,
            LocalDateTime end
    );
}
