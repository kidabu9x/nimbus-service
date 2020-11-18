package vn.com.nimbus.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Setter
@Getter
public class BlogCategoryID implements Serializable {
    private static final long serialVersionUID = -8247396434213551162L;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "blog_id")
    private Long blogId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogCategoryID that = (BlogCategoryID) o;
        return Objects.equals(blogId, that.blogId) &&
                Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, blogId);
    }
}
