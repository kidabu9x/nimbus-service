package vn.com.nimbus.blog.internal.model.response;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.data.domain.base.BlogExtraData;
import vn.com.nimbus.data.domain.constant.BlogStatus;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class BlogDetailResponse implements Serializable {
    private static final long serialVersionUID = 4844482821641360869L;
    private Long id;
    private String title;
    private String description;
    private String thumbnail;
    private BlogStatus status;
    private String content;
    private List<String> tags;
    private List<Long> categories;
    private BlogExtraData extraData;

    private Long updatedAt;
    private Long createdAt;

}
