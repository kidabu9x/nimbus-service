package vn.com.nimbus.blog.internal.model.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryResponse {
    private Long id;
    private String title;
    private Long totalBlogs;
}
