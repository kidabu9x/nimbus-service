package vn.com.nimbus.blog.api.service;

import vn.com.nimbus.blog.api.model.response.BasePublicResponse;
import vn.com.nimbus.blog.api.model.response.CategoryResponse;
import vn.com.nimbus.blog.api.model.response.FeatureResponse;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.paging.Paging;

import java.util.List;

public interface BlogService {
    Object getBlog(String blogSlug, LimitOffsetPageable limitOffsetPageable);

    FeatureResponse getFeature();

    Paging<BasePublicResponse> searchBlog(String title, LimitOffsetPageable limitOffsetPageable);

    List<CategoryResponse> getCategories();
}
