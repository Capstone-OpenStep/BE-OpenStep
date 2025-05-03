package com.chungang.capstone.openstep.domain.Repo.repository;

import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepoRepository extends JpaRepository<Repo, Long> {
    List<Repo> findTop10ByOrderByStarsDesc();
}

