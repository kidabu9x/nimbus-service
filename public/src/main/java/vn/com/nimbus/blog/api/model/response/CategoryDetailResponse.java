package vn.com.nimbus.blog.api.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class CategoryDetailResponse implements Serializable {
    private static final long serialVersionUID = -8859841232722079115L;

    private CategoryResponse category;
    private List<BlogResponse> blogs;
    private List<BlogResponse> highlights;
}
