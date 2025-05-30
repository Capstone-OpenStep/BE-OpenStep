package com.chungang.capstone.openstep.domain.Rank.repository;

import com.chungang.capstone.openstep.domain.Rank.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RankRepository extends JpaRepository<Rank, Long> {
    Optional<Rank> findByMember_MemberId(Long memberId);
    List<Rank> findAllByOrderByXpDesc();
}
