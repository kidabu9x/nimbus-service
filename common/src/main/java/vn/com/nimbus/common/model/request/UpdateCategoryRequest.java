package vn.com.nimbus.common.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateCategoryRequest extends CreateCategoryRequest {
    private Integer id;
}
