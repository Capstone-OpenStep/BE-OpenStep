package com.chungang.capstone.openstep.domain.Issue.repository;

import com.chungang.capstone.openstep.domain.Issue.entity.Issue;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    Optional<Issue> findByGithubUrl(String githubUrl);

    List<Issue> findAllByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String search);
    //List<Issue> findAllByMemberOrderByCreatedAtDesc(Member member);
    List<Issue> findAllBySummaryContainingIgnoreCaseOrderByCreatedAtDesc(String search);
}
