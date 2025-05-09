package com.chungang.capstone.openstep.domain.Repo.repository;

import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepoRepository extends JpaRepository<Repo, Long> {
    List<Repo> findTop10ByOrderByStarsDesc();
    Optional<Repo> findByGithubUrl(String githubUrl);
    Optional<Repo> findByRepoName(String name);

    List<Repo> findTop10ByRepoNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByStarsDesc(
            String nameKeyword, String descKeyword
    );

}

