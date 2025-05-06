package com.chungang.capstone.openstep.domain.Member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chungang.capstone.openstep.domain.Member.entity.Domain;
import com.chungang.capstone.openstep.domain.Member.entity.Member;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long> {
	public Domain findByName(String domainName);
}
