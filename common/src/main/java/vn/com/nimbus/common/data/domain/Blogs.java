package vn.com.nimbus.common.data.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.com.nimbus.common.data.domain.constant.BlogStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "blogs")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Blogs {
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BlogStatus status;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "extra_data")
    private String extraData;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "blog", fetch = FetchType.EAGER)
    private Set<BlogContents> contents;

    @OneToMany(mappedBy = "blog", fetch = FetchType.EAGER)
    private Set<BlogTag> tags;

    @OneToMany(mappedBy = "blog", fetch = FetchType.EAGER)
    private Set<BlogCategory> categories;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "blog_user", joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Users> authors;
}
