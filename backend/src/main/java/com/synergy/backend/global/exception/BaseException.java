package com.synergy.backend.global.exception;


import com.synergy.backend.global.common.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends Exception {
    private BaseResponseStatus status;

    // 기본 예외 메시지 확인
    public BaseException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    // 특정 예외 메시지 확인 (예상치 못한 경우)
    public BaseException(BaseResponseStatus status,Throwable cause) {
        super(cause);
        this.status = status;
    }
}