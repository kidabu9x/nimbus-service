package vn.com.nimbus.common.exception;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import vn.com.nimbus.common.model.error.BusinessErrorCode;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.error.FieldViolation;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.common.model.response.BaseResponse;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public final ResponseEntity<BaseResponse<Void>> handleException(BaseException exception) {
        log.warn(exception.getMessage());
        BaseResponse<Void> data = BaseResponse.ofFailed(exception);
        HttpStatus status = exception.getErrorCode().getHttpStatus();
        return new ResponseEntity<>(data, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccessDeniedException(AccessDeniedException exception) {
        log.warn(exception.getMessage());
        log.warn(ExceptionUtils.getStackTrace(exception));
        BusinessErrorCode errorCode = ErrorCode.FORBIDDEN;
        BaseResponse<Void> data = BaseResponse.ofFailed(errorCode, exception.getMessage());
        HttpStatus status = errorCode.getHttpStatus();
        return new ResponseEntity<>(data, status);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<BaseResponse<Void>> handleException(WebExchangeBindException exception) {
        log.warn(exception.getMessage());
        log.warn(ExceptionUtils.getStackTrace(exception));
        List<FieldViolation> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldViolation(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.toList());
        BusinessErrorCode errorCode = ErrorCode.INVALID_PARAMETERS;
        String errorMessage = "Invalid parameters of object: " + exception.getBindingResult().getObjectName();
        BaseResponse<Void> data = BaseResponse.ofFailed(errorCode, errorMessage, errors);
        HttpStatus status = errorCode.getHttpStatus();
        return new ResponseEntity<>(data, status);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<BaseResponse<Void>> handleException(ServerWebInputException exception) {
        log.warn(exception.getMessage());
        log.warn(ExceptionUtils.getStackTrace(exception));
        BaseResponse<Void> data = BaseResponse.ofFailed(ErrorCode.INVALID_PARAMETERS);
        return new ResponseEntity<>(data, exception.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception exception) {
        log.error(exception.getMessage());
        log.error(ExceptionUtils.getStackTrace(exception));
        BusinessErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        BaseResponse<Void> data = BaseResponse.ofFailed(errorCode, exception.getMessage());
        HttpStatus status = errorCode.getHttpStatus();
        return new ResponseEntity<>(data, status);
    }
}