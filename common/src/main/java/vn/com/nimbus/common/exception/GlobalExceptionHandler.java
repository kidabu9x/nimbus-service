package vn.com.nimbus.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.response.BaseResponseAdapter;
import vn.com.nimbus.common.model.response.BaseResponseAdapterImpl;
import vn.com.nimbus.common.model.response.ValidatorErrorMessage;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Resource
    ObjectMapper objectMapper;

    @ExceptionHandler({AppException.class, MethodArgumentNotValidException.class, HttpClientException.class, AppSecurityException.class, AppValidatorException.class, Exception.class})
    public final ResponseEntity handleException(Exception ex) {
        log.warn(ex.getMessage());
        BaseResponseAdapter responseAdapter;
        AppExceptionCode exceptionCode;
        if (ex instanceof AppException) {
            AppException billException = (AppException) ex;
            responseAdapter = new BaseResponseAdapterImpl(billException);
            exceptionCode = billException.getCode();
        } else if (ex instanceof HttpClientException) {
            HttpClientException httpClientException = (HttpClientException) ex;
            responseAdapter = new BaseResponseAdapterImpl(httpClientException);
            exceptionCode = httpClientException.getCode();
        } else if (ex instanceof AppSecurityException) {
            AppSecurityException securityException = (AppSecurityException) ex;
            responseAdapter = new BaseResponseAdapterImpl(securityException);
            exceptionCode = securityException.getCode();
        } else {
            log.error(ex.getMessage());
            responseAdapter = new BaseResponseAdapterImpl(new AppException(AppExceptionCode.INTERNAL_SERVER_ERROR));
            exceptionCode = AppExceptionCode.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(exceptionCode.getHttpStatusCode()).body(responseAdapter.getBaseResponse());

    }

    @ExceptionHandler({WebExchangeBindException.class})
    public final ResponseEntity handleException(WebExchangeBindException ex) {
        log.warn(ex.getMessage());
        AppExceptionCode exceptionCode = AppExceptionCode.BAD_REQUEST;
        List<ValidatorErrorMessage> errorMessages = ex.getFieldErrors().stream().map(fieldError -> {
            ValidatorErrorMessage message = new ValidatorErrorMessage();
            message.setFieldName(fieldError.getField());
            message.setErrorMessage(fieldError.getDefaultMessage());
            return message;
        }).collect(Collectors.toList());
        AppValidatorException validatorException = new AppValidatorException(errorMessages);
        BaseResponseAdapter responseAdapter = new BaseResponseAdapterImpl(validatorException);
        return ResponseEntity.status(exceptionCode.getHttpStatusCode()).body(responseAdapter.getBaseResponse());

    }

    //For filter before each rest api
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        log.warn(throwable.getMessage());
        if (throwable instanceof AppSecurityException) {
            AppSecurityException securityException = (AppSecurityException) throwable;
            BaseResponseAdapter responseAdapter = new BaseResponseAdapterImpl(securityException);
            try {
                DataBuffer buf = serverWebExchange.getResponse().bufferFactory().wrap(objectMapper.writeValueAsBytes(responseAdapter.getBaseResponse()));
                serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                serverWebExchange.getResponse().setStatusCode(HttpStatus.valueOf(securityException.getCode().getHttpStatusCode()));
                return serverWebExchange.getResponse().writeWith(Mono.just(buf));
            } catch (JsonProcessingException e) {
                log.error("Error Parse Json");
                return Mono.error(e);
            }
        }
        return Mono.error(throwable);
    }
}