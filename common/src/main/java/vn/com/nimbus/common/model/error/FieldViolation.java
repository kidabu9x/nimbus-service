package vn.com.nimbus.common.model.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class FieldViolation {
    private String field;
    private String description;
}
