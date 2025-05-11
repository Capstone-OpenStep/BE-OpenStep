package com.chungang.capstone.openstep.domain.Member.entity;

import com.chungang.capstone.openstep.domain.common.BaseEntity;

import com.chungang.capstone.openstep.domain.common.InterestDomain;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberDomain extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InterestDomain domain;
}
