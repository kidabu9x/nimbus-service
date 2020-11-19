package vn.com.nimbus.blog.api.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class BlogResponse implements Serializable {
    private static final long serialVersionUID = -1201063447891980385L;
    private Long id;

    private String slug;

    private String title;

    private String description;

    private String thumbnail;

    private List<Author> authors;

    private Long createdAt;

    private Long updatedAt;

    @Setter
    @Getter
    public static class Author implements Serializable {
        private static final long serialVersionUID = -2746198682875656702L;
        private String firstName;
        private String lastName;
        private String avatar;
    }
}
