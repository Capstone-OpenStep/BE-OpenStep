package com.chungang.capstone.openstep.domain.Rank.controller;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Rank.converter.RankConverter;
import com.chungang.capstone.openstep.domain.Rank.dto.RankResponseDTO;
import com.chungang.capstone.openstep.domain.Rank.service.RankQueryService;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;
import com.chungang.capstone.openstep.global.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rank")
@Tag(name = "순위 및 XP API", description = "사용자의 XP, 레벨 및 전체 랭킹 정보를 조회합니다.")
public class RankController {

    private final RankQueryService rankQueryService;

    @GetMapping("/xp")
    @Operation(summary = "사용자 XP 조회 API", description = "현재 로그인한 사용자의 XP를 조회합니다.")
    public ApiResponse<RankResponseDTO.MyXpDTO> getMyXp() {
        Member member = SecurityUtils.getCurrentMember();
        int xp = rankQueryService.getXp(member.getMemberId());
        return ApiResponse.onSuccess(SuccessStatus.RANK_GET_XP_OK, RankConverter.toMyXpDTO(xp));
    }

    @GetMapping("/level")
    @Operation(summary = "사용자 레벨 조회 API", description = "현재 로그인한 사용자의 레벨을 조회합니다.")
    public ApiResponse<RankResponseDTO.MyLevelDTO> getMyLevel() {
        Member member = SecurityUtils.getCurrentMember();
        int level = rankQueryService.getLevel(member.getMemberId());
        return ApiResponse.onSuccess(SuccessStatus.RANK_GET_LEVEL_OK, RankConverter.toMyLevelDTO(level));
    }

    @GetMapping("/all")
    @Operation(summary = "전체 랭킹 조회 API", description = "XP 기준으로 모든 사용자 랭킹을 내림차순 정렬하여 조회합니다.")
    public ApiResponse<RankResponseDTO.RankListDTO> getAllRanks() {
        List<RankResponseDTO.RankDTO> list = rankQueryService.getAllRankings().stream()
                .map(RankConverter::toRankDTO)
                .toList();
        return ApiResponse.onSuccess(SuccessStatus.RANK_GET_ALL_OK, new RankResponseDTO.RankListDTO(list));
    }
}
