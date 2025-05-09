package com.chungang.capstone.openstep.global.apiPayload.exception;

import com.chungang.capstone.openstep.global.apiPayload.code.BaseErrorCode;

public class GithubGraphQLException extends GeneralException {

    public GithubGraphQLException(BaseErrorCode code) {
        super(code);
    }
}