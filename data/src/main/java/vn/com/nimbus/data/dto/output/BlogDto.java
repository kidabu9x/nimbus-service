package vn.com.nimbus.data.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.data.domain.Blog;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class BlogDto implements Serializable {
    private static final long serialVersionUID = -5504470176727967115L;
    private Blog blog;
    private String slug;
}
