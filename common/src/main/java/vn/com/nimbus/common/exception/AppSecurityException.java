package vn.com.nimbus.common.exception;

import java.util.Objects;

public class AppSecurityException extends RuntimeException{

    private AppExceptionCode code;

    public AppSecurityException(AppExceptionCode code) {
        super(Objects.nonNull(code.getSubErrorMessage()) ? code.getSubErrorMessage() : code.getParentErrorMessage());
        this.code = code;
    }

    public AppSecurityException(AppExceptionCode code, Throwable throwable) {
        super(throwable);
        this.code = code;
    }

    public AppExceptionCode getCode() {
        return code;
    }

}
