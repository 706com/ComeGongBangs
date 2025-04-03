package com.synergy.backend.global.Handler;

import com.synergy.backend.global.common.BaseResponse;
import com.synergy.backend.global.common.BaseResponseStatus;
import com.synergy.backend.global.exception.BaseException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 400
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {
        // 로그는 자세히 남기고
        log.error("BaseException 발생: status={}, message={}",
                e.getStatus(), e.getMessage());
        return new ResponseEntity<>(new BaseResponse<>(e.getStatus()), HttpStatus.BAD_REQUEST);
    }

    // 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse<?>> handle404(NoHandlerFoundException e) {
        return new ResponseEntity<>(new BaseResponse<>(BaseResponseStatus.NOT_FOUND),
                HttpStatus.NOT_FOUND);
    }

    // validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(new BaseResponse<>(
            BaseResponseStatus.INVALID_REQUEST,errors), HttpStatus.BAD_REQUEST);
    }

//    // 500
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<BaseResponse<String>> handleException(Exception e) {
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new BaseResponse<>(BaseResponseStatus.SERVER_ERROR));
//    }

}
