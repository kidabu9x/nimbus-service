package vn.com.nimbus.common.model.exception;

import lombok.Getter;
import vn.com.nimbus.common.model.error.BusinessErrorCode;

@Getter
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 3325411715378792478L;
    private BusinessErrorCode errorCode;

    public BaseException(BusinessErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseException(BusinessErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(BusinessErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
