package com.chungang.capstone.openstep.domain.Rank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RankResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyXpDTO {
        private int xp;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyLevelDTO {
        private int level;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankDTO {
        private Long memberId;
        private String githubId;
        private int xp;
        private int level;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankListDTO {
        private List<RankDTO> ranks;
    }
}
