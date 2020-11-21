package vn.com.nimbus.data.domain;


import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.data.domain.constant.BlogContentType;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "blog_content")
public class BlogContent implements Serializable {
    private static final long serialVersionUID = -8957725462023949246L;
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blog_id")
    private Long blogId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private BlogContentType type;

    @Column(name = "content")
    private String content;

    @Column(name = "position")
    private Integer position;
}
