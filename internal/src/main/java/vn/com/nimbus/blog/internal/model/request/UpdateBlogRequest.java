package vn.com.nimbus.blog.internal.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateBlogRequest extends CreateBlogRequest {
    private static final long serialVersionUID = -5885888962684061279L;
    private Integer id;
}
