package vn.com.nimbus.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidatorErrorMessage implements Serializable {

    private String fieldName;
    private String errorMessage;

    public ValidatorErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static ValidatorErrorMessage of(String fieldName, String errorMessage) {
        return new ValidatorErrorMessage(fieldName, errorMessage);
    }

    public static ValidatorErrorMessage of(String errorMessage) {
        return new ValidatorErrorMessage(errorMessage);
    }
}
