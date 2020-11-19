package vn.com.nimbus.blog.api.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FeatureResponse extends BasePublicResponse {

    List<Feature> features;

    @Getter
    @Setter
    public static class Feature {
        private CategoryResponse category;
        private List<BlogResponse> blogs;
    }

}
