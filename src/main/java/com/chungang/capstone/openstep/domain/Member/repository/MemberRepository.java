package com.chungang.capstone.openstep.domain.Member.repository;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Member findByGithubId(String githubId);
}
