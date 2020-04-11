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

    private String content;

    private Integer viewsCount;

    private List<BlogPublicResponse.Author> authors;

    private List<String> tags;

    private BlogExtraData extraData;

    @Setter
    @Getter
    public static class Author {
        private Integer id;

        private String firstName;

        private String lastName;

        private String email;

        private String avatar;
    }
}
