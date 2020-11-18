package vn.com.nimbus.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "blog_category")
@Getter
@Setter
public class BlogCategory implements Serializable {
    private static final long serialVersionUID = 4751211558221004658L;
    @EmbeddedId
    private BlogCategoryID id;

}
