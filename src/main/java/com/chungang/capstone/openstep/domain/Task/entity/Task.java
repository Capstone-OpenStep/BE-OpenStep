package com.chungang.capstone.openstep.domain.Task.entity;

import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.common.BaseEntity;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.TaskException;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private TaskStatus status; // ASSIGNED / PR_CREATED / MERGED 등

    private String branchName;

    private String prUrl;

    private String forkedUrl;

    public void updateStatus(TaskStatus resolvedStatus) {
        this.status = resolvedStatus;
    }

    public void updatePrUrl(String prUrl) {
        if (this.status == TaskStatus.FORKED) {
            throw new TaskException(ErrorStatus.TASK_PR_URL_UPDATE_ERROR_EXCEPT_FORKED);
        }
        this.prUrl = prUrl;
    }
}
