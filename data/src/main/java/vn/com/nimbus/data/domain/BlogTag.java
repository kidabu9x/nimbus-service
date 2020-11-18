package vn.com.nimbus.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "blog_tag")
@Getter
@Setter
public class BlogTag implements Serializable {
    private static final long serialVersionUID = 5058210659692166237L;
    @EmbeddedId
    private BlogTagID id;

}
