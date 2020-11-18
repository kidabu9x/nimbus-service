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
public class BlogTagID implements Serializable {
    private static final long serialVersionUID = -4270383711182089580L;

    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "blog_id")
    private Long blogId;

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
