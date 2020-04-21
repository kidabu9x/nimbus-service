package vn.com.nimbus.common.model.response;

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
        BasePublicResponse.Category category;
        List<BasePublicResponse.Blog> blogs;
    }

}
