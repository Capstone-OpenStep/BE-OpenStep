package com.chungang.capstone.openstep.domain.Rank.converter;

import com.chungang.capstone.openstep.domain.Rank.dto.RankResponseDTO;
import com.chungang.capstone.openstep.domain.Rank.entity.Rank;

public class RankConverter {

    public static RankResponseDTO.MyXpDTO toMyXpDTO(int xp) {
        return RankResponseDTO.MyXpDTO.builder()
                .xp(xp)
                .build();
    }

    public static RankResponseDTO.MyLevelDTO toMyLevelDTO(int level, int levelPercent) {
        return RankResponseDTO.MyLevelDTO.builder()
                .level(level)
                .levelPercent(levelPercent)
                .build();
    }

    public static RankResponseDTO.RankDTO toRankDTO(Rank rank, int rankPosition) {
        return RankResponseDTO.RankDTO.builder()
                .memberId(rank.getMember().getMemberId())
                .githubId(rank.getMember().getGithubId())
                .avatarUrl(rank.getMember().getProfileImageUrl())
                .xp(rank.getXp())
                .rank(rankPosition)
                .build();
    }
}
