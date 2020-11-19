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
public class BlogTagID implements Serializable {
    private static final long serialVersionUID = -4270383711182089580L;

    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "blog_id")
    private Long blogId;
}
