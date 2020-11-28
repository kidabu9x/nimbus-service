package vn.com.nimbus.blog.api.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class HachiumCourseResponse implements Serializable {
    private static final long serialVersionUID = -9204251802084586799L;
    private String url;
    private String title;
    private String image;
    private Long oldPrice;
    private Long price;
    private String author;
}
