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
@Table(name = "blog_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogAuthor implements Serializable {
    private static final long serialVersionUID = 6694412675285585523L;
    @EmbeddedId
    private BlogAuthorID id;
}
