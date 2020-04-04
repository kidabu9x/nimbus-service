package vn.com.nimbus.common.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class BlogAuthorID implements Serializable {
    @Column(name = "blog_id")
    private String blogId;

    @Column(name = "user_id")
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogAuthorID that = (BlogAuthorID) o;
        return Objects.equals(blogId, that.blogId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blogId, userId);
    }
}
