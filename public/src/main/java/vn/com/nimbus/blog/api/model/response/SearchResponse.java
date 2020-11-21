package vn.com.nimbus.blog.api.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SearchResponse implements Serializable {
    private static final long serialVersionUID = 1227534441370622667L;
    private List<BlogResponse> blogs;
    private List<BlogResponse> highlights;
}
