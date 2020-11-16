package vn.com.nimbus.blog.internal.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class CreateBlogRequest implements Serializable {
    private static final long serialVersionUID = -2979433259008078591L;

    private Integer userId;

    private String description;

    @NotEmpty
    @Size(min = 1, max=255)
    private String title;

    private String thumbnail;

    private List<String> tags;

    private String status;

    @Valid
    private List<Category> categories;

    @NotEmpty
    private List<@Valid Content> contents;

    private ExtraData extraData;

    @Setter
    @Getter
    public static class Content {
        private Integer id;
        @NotEmpty
        private String content;
        @NotEmpty
        private String type;
    }

    @Setter
    @Getter
    public static class ExtraData {
        private String facebookPixelId;
        private String googleAnalyticsId;
    }

    @Setter
    @Getter
    public static class Category {
        private Integer id;
        private String title;
    }
}
