package vn.com.nimbus.blog.api.service;

import org.springframework.data.domain.Page;
import vn.com.nimbus.blog.api.model.request.BlogRequest;
import vn.com.nimbus.blog.api.model.response.BlogResponse;

public interface BlogService {
    Page<BlogResponse> getBlogs(String title, Long categoryId, Integer limit, Integer offset);

    BlogResponse getBlog(Long blogId);

    BlogResponse saveBlog(BlogRequest request);

    boolean deleteBlog(Long blogId);
}
