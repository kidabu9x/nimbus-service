package vn.com.nimbus.blog.api.service;

import vn.com.nimbus.blog.api.model.response.*;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.paging.Paging;

import java.util.List;

public interface BlogService {
    BlogDetailResponse getBlog(String slug);

    Paging<CategoryDetailResponse> getCategory(String slug, LimitOffsetPageable limitOffsetPageable);

    Paging<TagDetailResponse> getTag(String slug, LimitOffsetPageable limitOffsetPageable);

    Paging<SearchResponse> searchBlog(String title, LimitOffsetPageable limitOffsetPageable);

    FeatureResponse getFeature();

    List<CategoryResponse> getCategories();
}
