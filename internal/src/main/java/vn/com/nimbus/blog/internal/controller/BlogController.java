package vn.com.nimbus.blog.internal.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import vn.com.nimbus.blog.internal.model.request.BlogRequest;
import vn.com.nimbus.blog.internal.model.response.BlogDetailResponse;
import vn.com.nimbus.blog.internal.model.response.BlogResponse;
import vn.com.nimbus.blog.internal.service.BlogService;
import vn.com.nimbus.common.model.constant.KeyConstant;
import vn.com.nimbus.common.model.response.BaseResponse;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/blogs")
@CrossOrigin
public class BlogController {
    @Resource
    private BlogService blogService;

    @GetMapping()
    public Mono<BaseResponse<List<BlogResponse>>> getBlogs(
            @RequestParam(name = "title", required = false, defaultValue = "") String title,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(name = "category_id", required = false, defaultValue = "") Long categoryId
    ) {
        return Mono
                .just(blogService.getBlogs(title, categoryId, limit, offset))
                .map(BaseResponse::ofSucceeded);
    }

    @GetMapping("/{blogId}")
    public Mono<BaseResponse<BlogDetailResponse>> getBlog(@PathVariable Long blogId) {
        return Mono
                .just(blogService.getBlog(blogId))
                .map(BaseResponse::ofSucceeded);
    }

    @PostMapping()
    public Mono<BaseResponse<BlogDetailResponse>> createBlog(
            ServerHttpResponse currentResponse,
            @Valid @RequestBody BlogRequest request
    ) {
        request.setUserId(this.getUserId(currentResponse));
        return Mono
                .just(blogService.saveBlog(request))
                .map(BaseResponse::ofSucceeded);
    }

    @PutMapping("/{blogId}")
    public Mono<BaseResponse<BlogDetailResponse>> updateBlog(
            ServerHttpResponse currentResponse,
            @Valid @RequestBody BlogRequest request,
            @PathVariable("blogId") Long blogId
    ) {
        request.setUserId(this.getUserId(currentResponse));
        request.setId(blogId);
        return Mono
                .just(blogService.saveBlog(request))
                .map(BaseResponse::ofSucceeded);
    }

    private Long getUserId(ServerHttpResponse currentResponse) {
        HttpHeaders headers = currentResponse.getHeaders();
        String userId = headers.getFirst(KeyConstant.X_USER_ID);
        assert userId != null;
        return Long.valueOf(userId);
    }

    @DeleteMapping("/{blogId}")
    public Mono<BaseResponse<Boolean>> deleteBlog(
            ServerHttpResponse currentResponse, @PathVariable("blogId") Long blogId
    ) {
        HttpHeaders headers = currentResponse.getHeaders();
        String userId = headers.getFirst(KeyConstant.X_USER_ID);
        assert userId != null;
        return Mono
                .just(blogService.deleteBlog(blogId))
                .map(BaseResponse::ofSucceeded);
    }
}
