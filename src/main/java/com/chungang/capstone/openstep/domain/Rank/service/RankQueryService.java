package com.chungang.capstone.openstep.domain.Rank.service;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Rank.dto.RankResponseDTO;
import com.chungang.capstone.openstep.domain.Rank.entity.Rank;
import com.chungang.capstone.openstep.domain.Rank.repository.RankRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.RankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
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

    public RankResponseDTO.MyLevelDTO getMyLevel(Long memberId) {
        Rank rank = rankRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new RankException(ErrorStatus.RANK_NOT_FOUND));

        int xp = rank.getXp();
        int level = (xp / 100) + 1;
        int levelPercent = xp % 100;
        int percentRemaining = 100 - levelPercent;

        return RankResponseDTO.MyLevelDTO.builder()
                .level(level)
                .levelPercent(levelPercent)
                .percentRemaining(percentRemaining)
                .build();
    }


    public List<Rank> getAllRankings() {
        return rankRepository.findAllByOrderByXpDesc();
    }
}
