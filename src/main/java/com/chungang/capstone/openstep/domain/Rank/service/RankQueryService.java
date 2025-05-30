package com.chungang.capstone.openstep.domain.Rank.service;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Rank.entity.Rank;
import com.chungang.capstone.openstep.domain.Rank.repository.RankRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.RankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankQueryService {

    private final RankRepository rankRepository;

    public int getXp(Long memberId) {
        Rank rank = rankRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new RankException(ErrorStatus.RANK_NOT_FOUND));
        return rank.getXp();
    }

    public int getLevel(Long memberId) {
        Rank rank = rankRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new RankException(ErrorStatus.RANK_NOT_FOUND));
        return rank.getLevel();
    }

    public List<Rank> getAllRankings() {
        return rankRepository.findAllByOrderByXpDesc();
    }
}
