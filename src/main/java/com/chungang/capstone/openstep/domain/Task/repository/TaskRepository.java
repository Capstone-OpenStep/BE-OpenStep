package com.chungang.capstone.openstep.domain.Task.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
	Task findByMemberAndIssue(Member member, Issue issue);

	List<Task> findAllByMember(Member member);

	long countByMemberAndStatus(Member member, TaskStatus status);

	boolean existsByMemberAndStatus(Member member, TaskStatus status);

	// 특정 상태의 Task 개수
	@Query("SELECT COUNT(t) FROM Task t WHERE t.member.memberId = :memberId AND t.status = :status")
	long countByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") TaskStatus status);

	// 특정 날짜 이후의 활동이 있었던 날들 조회
	@Query("SELECT DISTINCT DATE(t.updatedAt) FROM Task t WHERE t.member.memberId = :memberId AND t.updatedAt >= :fromDate ORDER BY DATE(t.updatedAt)")
	List<LocalDate> findDistinctActivityDatesByMemberIdSince(
		@Param("memberId") Long memberId,
		@Param("fromDate") LocalDateTime fromDate
	);

	// Explorer 업적용: distinct repo 개수
	@Query("SELECT COUNT(DISTINCT t.issue.repo) FROM Task t WHERE t.member.memberId = :memberId AND t.status IN :statuses")
	long countDistinctReposByMemberIdAndStatuses(
		@Param("memberId") Long memberId,
		@Param("statuses") List<TaskStatus> statuses
	);

	// Explorer 업적용: 특정 repo에서 작업 존재 여부
	@Query("SELECT COUNT(t) > 0 FROM Task t WHERE t.member.memberId = :memberId AND t.issue.repo.repoName = :repoName AND t.status IN :statuses")
	boolean existsByMemberIdAndRepoNameAndStatuses(
		@Param("memberId") Long memberId,
		@Param("repoName") String repoName,
		@Param("statuses") List<TaskStatus> statuses
	);

}
