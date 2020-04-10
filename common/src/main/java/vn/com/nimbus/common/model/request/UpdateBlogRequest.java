package vn.com.nimbus.common.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateBlogRequest extends CreateBlogRequest {
    private Integer id;
}
