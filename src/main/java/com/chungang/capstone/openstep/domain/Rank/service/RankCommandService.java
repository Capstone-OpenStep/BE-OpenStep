package com.chungang.capstone.openstep.domain.Rank.service;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Rank.entity.Rank;
import com.chungang.capstone.openstep.domain.Rank.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankCommandService {

    private final RankRepository rankRepository;

    public void addXp(Member member, int earnedXp) {
        Rank rank = rankRepository.findByMember_MemberId(member.getMemberId())
                .orElseThrow(() -> new IllegalStateException("랭크 정보 없음"));

        int newXp = rank.getXp() + earnedXp;
        int newLevel = (newXp / 100) + 1;

        rank.setXp(newXp);
        rank.setLevel(newLevel);
        rankRepository.save(rank);
    }
}
