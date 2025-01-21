package com.mirero.pwm.member.domain.exception;

import com.mirero.pwm.common.exception.BaseException;
import com.mirero.pwm.common.exception.ErrorCode;

public class MemberException extends BaseException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}