package com.chungang.capstone.openstep.domain.Member.repository;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
