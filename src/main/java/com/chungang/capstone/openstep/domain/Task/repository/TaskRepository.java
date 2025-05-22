package com.chungang.capstone.openstep.domain.Task.repository;

import java.util.List;
import java.util.Optional;

import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
	Task findByMemberAndIssue(Member member, Issue issue);

	List<Task> findAllByMember(Member member);
}
