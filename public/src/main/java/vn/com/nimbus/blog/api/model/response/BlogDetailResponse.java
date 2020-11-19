package vn.com.nimbus.blog.api.model.response;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.common.model.extra.BlogExtraData;

import java.util.List;

@Setter
@Getter
public class BlogDetailResponse extends BlogResponse {
    private String description;
    private String content;
    private BlogExtraData extraData;
    private List<CategoryResponse> categories;
    private List<TagResponse> tags;
}
