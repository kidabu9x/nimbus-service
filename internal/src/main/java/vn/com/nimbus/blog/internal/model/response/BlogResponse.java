package vn.com.nimbus.blog.internal.model.response;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.data.domain.constant.BlogStatus;

import java.io.Serializable;

@Getter
@Setter
public class BlogResponse implements Serializable {
    private static final long serialVersionUID = 2750374965909818789L;

    private Long id;

    private String title;

    private BlogStatus status;

    private Long createdAt;

    private Long updatedAt;
}
