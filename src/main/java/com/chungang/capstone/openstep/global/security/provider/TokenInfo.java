package com.chungang.capstone.openstep.global.security.provider;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Builder
@Data
@Getter
public class TokenInfo {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Date refreshTokenExpirationTime;
}
