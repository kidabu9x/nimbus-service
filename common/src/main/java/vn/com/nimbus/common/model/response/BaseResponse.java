package vn.com.nimbus.common.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import vn.com.nimbus.common.model.error.BusinessErrorCode;
import vn.com.nimbus.common.model.error.FieldViolation;
import vn.com.nimbus.common.model.exception.BaseException;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class BaseResponse<T> {
    public static final String OK_CODE = "200";
    private T data;
    private Metadata meta = new Metadata();

    public static <T> BaseResponse<T> ofSucceeded(T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.data = data;
        response.meta.code = OK_CODE;
        return response;
    }

    public static BaseResponse<Void> ofSucceeded() {
        BaseResponse<Void> response = new BaseResponse<>();
        response.meta.code = OK_CODE;
        return response;
    }

    public static <T> BaseResponse<List<T>> ofSucceeded(Page<T> data) {
        BaseResponse<List<T>> response = new BaseResponse<>();
        response.data = data.getContent();
        response.meta.code = OK_CODE;
        response.meta.page = data.getPageable().getPageNumber();
        response.meta.size = data.getPageable().getPageSize();
        response.meta.total = data.getTotalElements();
        return response;
    }

    public static BaseResponse<Void> ofFailed(BusinessErrorCode errorCode) {
        return ofFailed(errorCode, null);
    }

    public static BaseResponse<Void> ofFailed(BusinessErrorCode errorCode, String message) {
        return ofFailed(errorCode, message, errorCode.getErrors());
    }

    public static BaseResponse<Void> ofFailed(BusinessErrorCode errorCode, String message, List<FieldViolation> errors) {
        BaseResponse<Void> response = new BaseResponse<>();
        response.meta.code = String.valueOf(errorCode.getCode());
        response.meta.message = message != null ? message : errorCode.getMessage();
        response.meta.errors = errors;
        return response;
    }

    public static BaseResponse<Void> ofFailed(BaseException exception) {
        return ofFailed(exception.getErrorCode(), exception.getMessage());
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Metadata {
        String code;
        Integer page;
        Integer size;
        Long total;
        List<FieldViolation> errors;
        String message;
        Boolean hasData;
    }

}
