package com.chungang.capstone.openstep.domain.Repo.service;

import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Repo.repository.RepoRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepoQueryService {

    private final RepoRepository repoRepository;

    public List<Repo> getTrendingRepos() {
        return repoRepository.findTop10ByOrderByStarsDesc();
    }

    public Repo getRepoById(Long repoId) {
        return repoRepository.findById(repoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REPO_NOT_FOUND));
    }
}
