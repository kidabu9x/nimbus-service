package vn.com.nimbus.blog.api.service;

import vn.com.nimbus.blog.api.model.response.CategoryResponse;
import vn.com.nimbus.blog.api.model.response.FeatureResponse;
import vn.com.nimbus.blog.api.model.response.HachiumCourseResponse;
import vn.com.nimbus.blog.api.model.response.SearchResponse;
import vn.com.nimbus.blog.api.model.response.TagDetailResponse;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.paging.Paging;

import java.util.List;

public interface BlogService {
    Paging<Object> getBySlug(String slug, LimitOffsetPageable limitOffsetPageable);

    Paging<TagDetailResponse> getTag(String slug, LimitOffsetPageable limitOffsetPageable);

    Paging<SearchResponse> searchBlog(String title, LimitOffsetPageable limitOffsetPageable);

    FeatureResponse getFeature();

    List<CategoryResponse> getCategories();

    List<HachiumCourseResponse> getCourses(String slug);
}
