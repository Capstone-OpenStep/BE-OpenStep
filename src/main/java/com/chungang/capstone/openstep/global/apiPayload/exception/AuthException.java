package com.chungang.capstone.openstep.global.apiPayload.exception;

import com.chungang.capstone.openstep.global.apiPayload.code.BaseErrorCode;

public class AuthException extends GeneralException {

    public AuthException(BaseErrorCode code) {
        super(code);
    }
}