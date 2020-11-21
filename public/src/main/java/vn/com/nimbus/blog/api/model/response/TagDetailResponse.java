package vn.com.nimbus.blog.api.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class TagDetailResponse implements Serializable {
    private static final long serialVersionUID = -3951487637376808791L;
    private TagResponse tag;
    private List<BlogResponse> blogs;
    private List<BlogResponse> highlights;
}
