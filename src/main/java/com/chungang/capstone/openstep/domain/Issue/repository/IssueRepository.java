package com.chungang.capstone.openstep.domain.Issue.repository;

import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
}
