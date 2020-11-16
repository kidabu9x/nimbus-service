package vn.com.nimbus.common.service;

import vn.com.nimbus.data.domain.Blogs;

public interface BlogViewService {
    void addView(Blogs blog);
    void deleteByBlogId(Integer blogId);
    long countByBlogId(Integer blogId);
}
