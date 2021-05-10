package vn.com.nimbus.common.model.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class BusinessErrorCode {
    private int code;
    private String message;
    private HttpStatus httpStatus;
    private List<FieldViolation> errors;


    public BusinessErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.httpStatus = status;
    }

    public BusinessErrorCode(int code, String message, HttpStatus status, List<FieldViolation> errors) {
        this.code = code;
        this.message = message;
        this.httpStatus = status;
        this.errors = errors;
    }
}
