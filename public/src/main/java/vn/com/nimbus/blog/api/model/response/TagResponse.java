package vn.com.nimbus.blog.api.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TagResponse implements Serializable {
    private static final long serialVersionUID = 6253872379224258761L;
    private Integer id;
    private String title;
    private String slug;
}
