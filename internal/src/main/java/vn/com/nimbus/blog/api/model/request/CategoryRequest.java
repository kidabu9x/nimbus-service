package vn.com.nimbus.blog.api.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Setter
@Getter
public class CategoryRequest {
    @JsonIgnore
    private Integer id;

    @NotEmpty
    @Size(min = 1, max=50)
    private String title;
}
