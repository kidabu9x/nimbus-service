package vn.com.nimbus.common.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseResponseMessage {
    private Meta meta;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties( { "failReason" })
    public static class Meta implements Serializable {
        //
        private int code;
        private String message;
        private String failReason;
        private List<Error> errors = new ArrayList<>();

        public Meta(int code, String message) {
            this.code = code;
            this.message = message;
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Error implements Serializable {

        private String code;

        private String message;

        public Error(String message) {
            this.message = message;
        }

    }
}
