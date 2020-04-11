package vn.com.nimbus.common.model.response;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.common.model.extra.BlogExtraData;

import java.util.List;

@Setter
@Getter
public class BlogPublicResponse {
    private Integer id;

    private String slug;

    private String title;

    private String description;

    private String thumbnail;

    private String updatedAt;

    private List<Author> authors;

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
