package vn.com.nimbus.blog.internal.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Setter
@Getter
public class CreateCategoryRequest {

    @NotEmpty
    @Size(min = 1, max=50)
    private String title;
}
