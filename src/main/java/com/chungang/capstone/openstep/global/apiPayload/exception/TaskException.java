package com.chungang.capstone.openstep.global.apiPayload.exception;

import com.chungang.capstone.openstep.global.apiPayload.code.BaseErrorCode;

public class TaskException extends GeneralException {
	public TaskException(BaseErrorCode code) {
		super(code);
	}
}
