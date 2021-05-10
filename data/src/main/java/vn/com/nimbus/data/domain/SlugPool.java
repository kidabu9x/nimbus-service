package vn.com.nimbus.data.domain;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.com.nimbus.data.domain.constant.SlugPoolType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "slug_pool")
@EntityListeners(AuditingEntityListener.class)
@Data
public class SlugPool implements Serializable {
    private static final long serialVersionUID = -2150257816352471176L;
    @Id
    @Column(name = "slug", unique = true, updatable = false, nullable = false)
    private String slug;

    @Column(name = "type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private SlugPoolType type;

    @Column(name = "target_id", nullable = false, updatable = false)
    private Long targetId;
}
