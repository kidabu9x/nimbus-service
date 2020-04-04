package vn.com.nimbus.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseResponse implements Serializable {

    private Object data;
    private Meta meta;

    @Getter
    @Setter
    @ToString
    @Builder
    public static class Meta implements Serializable {
        private List<ErrorMessageCode> errors;
        private int code;
        private String message;
        private Integer page;
        private Integer pageSize;
        private Integer limit;
        private Integer offset;
        private Long total;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class ErrorMessageCode implements Serializable {

        private Integer code;
        private String target;
        private String message;

        public ErrorMessageCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public ErrorMessageCode(String target, String message) {
            this.target = target;
            this.message = message;
        }
    }
}
