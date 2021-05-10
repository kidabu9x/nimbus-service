package vn.com.nimbus.data.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogAuthorID implements Serializable {
    private static final long serialVersionUID = -1760412042089743639L;
    // TODO: need confirm this when test
    @Column(name = "user_id")
    private Long authorId;

    @Column(name = "blog_id")
    private Long blogId;
}
