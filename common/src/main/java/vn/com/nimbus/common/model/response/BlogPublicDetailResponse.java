package vn.com.nimbus.common.model.response;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.common.model.extra.BlogExtraData;

import java.util.List;

@Setter
@Getter
public class BlogPublicDetailResponse extends BasePublicResponse{

    private String description;

    private String thumbnail;

    private String updatedAt;

    private List<Content> contents;

    private long viewsCount;

    private List<Author> authors;

    private List<String> tags;

    private BlogExtraData extraData;

    private String readingTime;

    @Getter
    @Setter
    public static class Content {
        private Integer id;

        private String content;
    }
}
