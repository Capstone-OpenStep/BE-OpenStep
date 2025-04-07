package com.chungang.capstone.openstep.global.apiPayload.exception.handler;

import com.chungang.capstone.openstep.global.apiPayload.code.BaseErrorCode;
import com.chungang.capstone.openstep.global.apiPayload.exception.GeneralException;

public class MemberException extends GeneralException {
    public MemberException(BaseErrorCode code) {
        super(code);
    }
}