package vn.com.nimbus.blog.internal.service;

import org.springframework.data.domain.Page;
import vn.com.nimbus.blog.internal.model.request.CreateBlogRequest;
import vn.com.nimbus.blog.internal.model.request.UpdateBlogRequest;
import vn.com.nimbus.blog.internal.model.response.BlogResponse;

public interface BlogService {
    Page<BlogResponse> getBlogs(String title, Integer categoryId, Integer limit, Integer offset);

    BlogResponse getBlog(Integer blogId);

    BlogResponse createBlog(CreateBlogRequest request);

    BlogResponse updateBlog(UpdateBlogRequest request);

    boolean deleteBlog(Integer blogId);
}
