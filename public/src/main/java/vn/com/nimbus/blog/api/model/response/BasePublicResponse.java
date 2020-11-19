package vn.com.nimbus.blog.api.model.response;


import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.data.domain.constant.PublicResponseType;

import java.util.List;

@Setter
@Getter
public class BasePublicResponse {
    private PublicResponseType type;
    private BlogDetailResponse blog;
    private CategoryResponse category;
    private TagResponse tag;
    private List<BlogResponse> blogs;
    private List<BlogResponse> highlights;
}
