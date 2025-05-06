package com.chungang.capstone.openstep.domain.Issue.repository;

import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    Optional<Issue> findByGithubUrl(String githubUrl);
}
