package vn.com.nimbus.common.model.response;


import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.common.data.domain.constant.PublicResponseType;

import java.util.List;

@Setter
@Getter
public class BasePublicResponse {

    private Integer id;

    private String slug;

    private String title;

    private PublicResponseType type;

    List<Blog> blogs;

    @Setter
    @Getter
    public static class Blog {
        private Integer id;
        private String title;
        private String slug;
        private String thumbnail;
        private String description;
        private String readingTime;
        private String updatedAt;
        private long viewCount;
        private long commentCount;

        private List<Author> authors;
        private List<Category> categories;
        private List<Tag> tags;
    }

    @Setter
    @Getter
    public static class Author {
        private Integer id;
        private String avatar;
        private String firstName;
        private String lastName;
    }

    @Setter
    @Getter
    public static class Category {
        private Integer id;
        private String title;
        private String slug;
    }

    @Setter
    @Getter
    public static class Tag {
        private String title;
        private String slug;
    }
}
