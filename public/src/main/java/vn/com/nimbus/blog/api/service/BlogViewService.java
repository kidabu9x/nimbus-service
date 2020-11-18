package vn.com.nimbus.blog.api.service;

import vn.com.nimbus.data.domain.Blog;

public interface BlogViewService {
    void addView(Blog blog);
    void deleteByBlogId(Integer blogId);
    long countByBlogId(Integer blogId);
}
