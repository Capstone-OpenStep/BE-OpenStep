package com.chungang.capstone.openstep.global.apiPayload.exception;

import com.chungang.capstone.openstep.global.apiPayload.code.BaseErrorCode;

public class GithubRestException extends GeneralException {

    public GithubRestException(BaseErrorCode code) {
        super(code);
    }
}