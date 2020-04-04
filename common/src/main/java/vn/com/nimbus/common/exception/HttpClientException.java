package vn.com.nimbus.common.exception;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.common.model.response.BaseResponseMessage;

import java.util.Objects;

@Getter
@Setter
public class HttpClientException extends RuntimeException {
    protected String name;
    protected int statusCode;
    protected String defaultMessage;
    protected String connectorMessage;

    protected BaseResponseMessage.Meta meta;

    private AppExceptionCode code;

    public HttpClientException(AppExceptionCode code) {
        super(Objects.nonNull(code.getSubErrorMessage()) ? code.getSubErrorMessage() : code.getParentErrorMessage());
        this.code = code;
    }

    public HttpClientException(AppExceptionCode code, Throwable throwable) {
        super(throwable);
        this.code = code;
    }
}
