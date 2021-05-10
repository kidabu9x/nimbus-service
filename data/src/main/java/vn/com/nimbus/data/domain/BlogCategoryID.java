package vn.com.nimbus.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BlogCategoryID implements Serializable {
    private static final long serialVersionUID = -8247396434213551162L;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "blog_id")
    private Long blogId;
}
