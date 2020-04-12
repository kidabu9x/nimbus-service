package vn.com.nimbus.common.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.paging.Paging;
import vn.com.nimbus.common.model.request.CreateBlogRequest;
import vn.com.nimbus.common.model.request.UpdateBlogRequest;
import vn.com.nimbus.common.model.response.BlogResponse;

public interface BlogService {
    Mono<Paging<BlogResponse>> getBlogs(String title, Integer limit, Integer offset);

    Mono<BlogResponse> getBlog(Integer blogId);

    Mono<BlogResponse> createBlog(CreateBlogRequest request);

    void updateBlog(UpdateBlogRequest request);

    void deleteBlog(Integer blogId);
}
