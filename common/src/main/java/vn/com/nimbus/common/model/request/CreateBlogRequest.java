package vn.com.nimbus.common.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
public class CreateBlogRequest {
    private Integer userId;

    @NotEmpty
    @Size(min = 1, max=255)
    private String title;

    private String thumbnail;

    private List<String> tags;

    private String status;

    @Valid
    @NotEmpty
    private List<Content> contents;
    private List<Integer> categoryIds;

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
    }
}
