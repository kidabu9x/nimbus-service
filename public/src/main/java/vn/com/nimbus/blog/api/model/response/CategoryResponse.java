package vn.com.nimbus.blog.api.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CategoryResponse implements Serializable {
    private static final long serialVersionUID = -7055864014034887150L;
    private Long id;
    private String title;
    private String slug;
}
