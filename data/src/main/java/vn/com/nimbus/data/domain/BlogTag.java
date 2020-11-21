package vn.com.nimbus.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "blog_tag")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogTag implements Serializable {
    private static final long serialVersionUID = 5058210659692166237L;
    @EmbeddedId
    private BlogTagID id;

}
