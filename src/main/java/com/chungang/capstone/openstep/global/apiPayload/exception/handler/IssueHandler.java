package com.chungang.capstone.openstep.global.apiPayload.exception.handler;

import com.chungang.capstone.openstep.global.apiPayload.code.BaseErrorCode;
import com.chungang.capstone.openstep.global.apiPayload.exception.GeneralException;

public class IssueHandler extends GeneralException {
    public IssueHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
