package vn.com.nimbus.blog.internal.service;

import org.springframework.data.domain.Page;
import vn.com.nimbus.blog.internal.model.request.BlogRequest;
import vn.com.nimbus.blog.internal.model.response.BlogDetailResponse;
import vn.com.nimbus.blog.internal.model.response.BlogResponse;

public interface BlogService {
    Page<BlogResponse> getBlogs(String title, Long categoryId, Integer limit, Integer offset);

    BlogDetailResponse getBlog(Long blogId);

    BlogDetailResponse saveBlog(BlogRequest request);

    boolean deleteBlog(Long blogId);
}
