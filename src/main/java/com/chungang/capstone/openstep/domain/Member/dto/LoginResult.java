package com.chungang.capstone.openstep.domain.Member.dto;

import com.chungang.capstone.openstep.domain.Member.entity.Member;

import lombok.Builder;

@Builder
public record LoginResult(Member member, boolean isNewUser,String accessToken) {}