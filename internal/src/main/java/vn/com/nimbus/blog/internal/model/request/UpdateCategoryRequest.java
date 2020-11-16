package vn.com.nimbus.blog.internal.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateCategoryRequest extends CreateCategoryRequest {
    private Integer id;
}
