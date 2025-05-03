package com.chungang.capstone.openstep.global.apiPayload.exception.handler;

import com.chungang.capstone.openstep.global.apiPayload.code.BaseErrorCode;
import com.chungang.capstone.openstep.global.apiPayload.exception.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class RepoHandler extends GeneralException {
    public RepoHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
