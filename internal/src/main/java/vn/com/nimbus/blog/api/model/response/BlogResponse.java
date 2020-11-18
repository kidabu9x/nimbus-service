package vn.com.nimbus.blog.api.model.response;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.blog.api.model.request.BlogRequest;
import vn.com.nimbus.data.domain.constant.BlogStatus;
import vn.com.nimbus.common.model.extra.BlogExtraData;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class BlogResponse extends BlogRequest {
    private static final long serialVersionUID = 4844482821641360869L;
    private Long id;
    private String title;
    private String slug;
    private BlogStatus status;
    private String thumbnail;
    private String content;
    private List<String> tags;
    private List<Long> categories;
    private String description;
    private BlogExtraData extraData;

    private Long updatedAt;
    private Long createdAt;

}
