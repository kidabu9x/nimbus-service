package vn.com.nimbus.data.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.com.nimbus.data.domain.base.BlogExtraData;
import vn.com.nimbus.data.domain.constant.BlogStatus;
import vn.com.nimbus.data.domain.converter.JsonAttributeConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "blog")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Blog implements Serializable {
    private static final long serialVersionUID = 601568770707488920L;
    @Id
    @Column(name = "id", unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BlogStatus status;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "extra_data")
    @Convert(converter = JsonAttributeConverter.BlogExtraDataConverter.class)
    private BlogExtraData extraData;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
