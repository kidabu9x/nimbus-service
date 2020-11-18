package vn.com.nimbus.blog.api.model.response;


import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.data.domain.constant.PublicResponseType;
import vn.com.nimbus.common.model.extra.BlogExtraData;

import java.util.List;

@Setter
@Getter
public class BasePublicResponse {
    private PublicResponseType type;
    private BlogDetail blog;
    private Category category;
    private Tag tag;
    List<Blog> blogs;
    List<Blog> highlights;

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
        private long viewsCount;
        private long commentCount;
        private List<Author> authors;
        private List<Category> categories;
        private List<Tag> tags;
    }

    @Setter
    @Getter
    public static class BlogDetail extends Blog{
        private String description;
        private List<Content> contents;
        private BlogExtraData extraData;
    }

    @Getter
    @Setter
    public static class Content {
        private Integer id;

        private String content;
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
        private Integer id;
        private String title;
        private String slug;
    }
}
