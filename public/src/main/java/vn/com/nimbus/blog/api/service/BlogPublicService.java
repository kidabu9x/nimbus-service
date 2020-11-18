package vn.com.nimbus.blog.api.service;

import org.springframework.data.domain.Page;
import vn.com.nimbus.blog.api.model.response.BasePublicResponse;
import vn.com.nimbus.blog.api.model.response.CategoryResponse;
import vn.com.nimbus.blog.api.model.response.FeatureResponse;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;

import java.util.List;

public interface BlogPublicService {
    Object getBlog(String blogSlug, LimitOffsetPageable limitOffsetPageable);

    FeatureResponse getFeature();

    Page<BasePublicResponse> searchBlog(String title, LimitOffsetPageable limitOffsetPageable);

    List<CategoryResponse> getCategories();
}
