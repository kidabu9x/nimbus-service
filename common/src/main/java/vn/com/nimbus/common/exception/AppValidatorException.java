package vn.com.nimbus.common.exception;

import vn.com.nimbus.common.model.response.ValidatorErrorMessage;

import java.util.List;

public class AppValidatorException extends AppException {
    private List<ValidatorErrorMessage> errorMessages;

    public AppValidatorException(AppExceptionCode code) {
        super(code);
    }

    public AppValidatorException(AppExceptionCode code, Throwable throwable) {
        super(code, throwable);
    }

    public AppValidatorException(List<ValidatorErrorMessage> errorMessages) {
        super(AppExceptionCode.BAD_REQUEST);
        this.errorMessages = errorMessages;
    }

    public AppValidatorException(List<ValidatorErrorMessage> errorMessages, AppExceptionCode code) {
        super(code);
        this.errorMessages = errorMessages;
    }

    public List<ValidatorErrorMessage> getErrorMessages() {
        return errorMessages;
    }
}
