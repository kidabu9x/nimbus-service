package vn.com.nimbus.common.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.request.CreateBlogRequest;
import vn.com.nimbus.common.model.response.BlogResponse;

public interface BlogService {
    Flux<BlogResponse> getBlogs();

    Mono<BlogResponse> getBlog(Integer blogId);

    Mono<BlogResponse> createBlog(Integer userId, CreateBlogRequest request);

    void deleteBlog(Integer blogId);
}
