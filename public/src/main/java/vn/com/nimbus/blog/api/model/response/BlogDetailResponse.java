package vn.com.nimbus.blog.api.model.response;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.data.domain.base.BlogExtraData;

import java.util.List;

@Setter
@Getter
public class BlogDetailResponse extends BlogResponse {
    private static final long serialVersionUID = -7728854346124996350L;

    private String type = "BLOG";
    private String description;
    private String content;
    private BlogExtraData extraData;
    private List<CategoryResponse> categories;
    private List<TagResponse> tags;

    private List<BlogResponse> highlights;
}
