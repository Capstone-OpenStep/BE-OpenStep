package com.chungang.capstone.openstep.domain.Member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chungang.capstone.openstep.domain.Member.entity.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill,Long> {
	Skill findByName(String name);
}
