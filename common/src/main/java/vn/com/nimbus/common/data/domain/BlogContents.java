package vn.com.nimbus.common.data.domain;


import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.common.data.domain.constant.BlogContentType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "blog_contents")
public class BlogContents {
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "blog_id")
    private Integer blogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", insertable = false, updatable = false)
    private Blogs blog;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private BlogContentType type;

    @Column(name = "content")
    private String content;

    @Column(name = "position")
    private Integer position;
}
