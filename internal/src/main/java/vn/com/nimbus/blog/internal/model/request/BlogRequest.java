package vn.com.nimbus.blog.internal.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.data.domain.base.BlogExtraData;
import vn.com.nimbus.data.domain.constant.BlogStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
public class BlogRequest implements Serializable {
    private static final long serialVersionUID = -2979433259008078591L;
    @JsonIgnore
    private Long id;

    @JsonIgnore
    private Long userId;

    private String description;

    @NotEmpty
    @Size(min = 1, max=255)
    private String title;

    private String thumbnail;

    private Set<String> tags;

    private BlogStatus status;

    private Set<Long> categories;

    @NotEmpty
    private String content;

    private BlogExtraData extraData;

}
