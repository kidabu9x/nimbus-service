package vn.com.nimbus.common.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class AppException extends RuntimeException {

    private AppExceptionCode code;

    public AppException(AppExceptionCode code) {
        super(Objects.nonNull(code.getSubErrorMessage()) ? code.getSubErrorMessage() : code.getParentErrorMessage());
        this.code = code;
    }

    public AppException(AppExceptionCode code, Throwable throwable) {
        super(throwable);
        this.code = code;
    }
}