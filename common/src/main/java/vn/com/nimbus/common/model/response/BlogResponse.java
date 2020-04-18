package vn.com.nimbus.common.model.response;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.common.data.domain.constant.BlogContentType;
import vn.com.nimbus.common.data.domain.constant.BlogStatus;
import vn.com.nimbus.common.model.extra.BlogExtraData;

import java.util.List;

@Setter
@Getter
public class BlogResponse {
    private Integer id;
    private String title;
    private String slug;
    private BlogStatus status;
    private String thumbnail;
    private List<Content> contents;
    private List<String> tags;
    private List<Author> authors;
    private List<Category> categories;
    private String updatedAt;
    private String description;

    private BlogExtraData extraData;

    @Setter
    @Getter
    public static class Content {
        private Integer id;
        private String content;
        private BlogContentType type;
        private Integer position;
    }

    @Setter
    @Getter
    public static class Author {
        private Integer id;
        private String email;
        private String firstName;
        private String lastName;
        private String avatar;
    }

    @Setter
    @Getter
    public static class Category {
        private Integer id;
        private String title;
    }
}
