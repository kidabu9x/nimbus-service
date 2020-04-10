package vn.com.nimbus.common.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Setter
@Getter
public class BlogTagID implements Serializable {
    @Column(name = "tag_id")
    private Integer tagId;

    @Column(name = "blog_id")
    private Integer blogId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogTagID that = (BlogTagID) o;
        return Objects.equals(blogId, that.blogId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, blogId);
    }
}
