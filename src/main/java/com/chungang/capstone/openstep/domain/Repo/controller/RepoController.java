package com.chungang.capstone.openstep.domain.Repo.controller;

import com.chungang.capstone.openstep.domain.Repo.service.RepoQueryService;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.domain.Repo.converter.RepoConverter;
import com.chungang.capstone.openstep.domain.Repo.dto.RepoResponseDTO;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.global.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/repo")
@Tag(name = "레포지토리 API", description = "레포지토리 관련 API입니다.")
public class RepoController {

    private final RepoQueryService repoQueryService;

    // 트렌딩 레포지토리 목록 조회 API
    @GetMapping("/trending")
    @Operation(summary = "트렌딩 레포지토리 조회 API", description = "현재 인기 있는 트렌딩한 오픈소스 레포지토리를 조회합니다.")
    public ApiResponse<List<RepoResponseDTO.TrendingRepoDTO>> getTrendingRepos() {
        List<Repo> repos = repoQueryService.getTrendingRepos();
        return ApiResponse.onSuccess(SuccessStatus.REPO_GET_TRENDING_OK, RepoConverter.toTrendingDTOs(repos));
    }

    // 특정 레포지토리 상세 조회
    @GetMapping("/{repo-id}")
    @Operation(summary = "특정 레포지토리 상세 조회 API", description = "특정 오픈소스 레포지토리의 상세 정보를 조회합니다.")
    public ApiResponse<RepoResponseDTO.RepoDetailDTO> getRepoDetail(@PathVariable("repo-id") Long repoId) {
        Repo repo = repoQueryService.getRepoById(repoId);
        return ApiResponse.onSuccess(SuccessStatus.REPO_GET_DETAIL_OK, RepoConverter.toRepoDetailDTO(repo));
    }

    // 이름으로 레포지토리 검색
    @GetMapping("/search/name")
    @Operation(summary = "레포지토리 이름 검색 API", description = "입력한 키워드로 오픈소스 레포지토리를 검색합니다. 결과는 최대 10개까지 반환됩니다.")
    public ApiResponse<RepoResponseDTO.RepoListDTO> searchReposByName(
            @RequestParam Optional<String> search) {

        List<Repo> repos = repoQueryService.getReposByName(Optional.of(search.orElse("")));
        return ApiResponse.onSuccess(SuccessStatus.REPO_GET_LIST_BY_NAME_OK, RepoConverter.toRepoListDTO(repos));
    }

    @GetMapping("/suggest")
    @Operation(summary = "사용자 맞춤 레포지토리 추천 API", description = "사용자의 관심사에 맞는 오픈소스 레포지토리를 추천합니다.")
    public ApiResponse<RepoResponseDTO.RepoListDTO> suggestRepos(@RequestParam Long memberId) {
        List<Repo> repos = repoQueryService.getSuggestedReposBySplitQuery(memberId);
        return ApiResponse.onSuccess(SuccessStatus.REPO_GET_SUGGEST_OK, RepoConverter.toRepoListDTO(repos));
    }




}


