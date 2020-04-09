package vn.com.nimbus.common.model.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryResponse {
    private Integer id;
    private String title;
    private String slug;
    private Long totalBlogs;
}
