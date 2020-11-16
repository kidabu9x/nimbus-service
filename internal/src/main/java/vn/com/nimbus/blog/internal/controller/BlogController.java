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
import vn.com.nimbus.common.controller.AbstractController;
import vn.com.nimbus.common.model.constant.KeyConstant;
import vn.com.nimbus.common.model.request.CreateBlogRequest;
import vn.com.nimbus.common.model.request.UpdateBlogRequest;
import vn.com.nimbus.common.model.response.BaseResponse;
import vn.com.nimbus.common.service.BlogService;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/blogs")
@CrossOrigin
public class BlogController extends AbstractController {
    @Resource
    private BlogService blogService;

    @GetMapping()
    public Mono<BaseResponse> getBlogs(
            @RequestParam(name = "title", required = false, defaultValue = "") String title,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(name = "category_id", required = false, defaultValue = "") Integer categoryId
    ) {
        return processBaseResponse(blogService.getBlogs(title, categoryId, limit, offset));
    }

    @GetMapping("/{blogId}")
    public Mono<BaseResponse> getBlog(@PathVariable Integer blogId) {
        return processBaseResponse(blogService.getBlog(blogId));
    }

    @PostMapping()
    public Mono<BaseResponse> createBlog(ServerHttpResponse currentResponse, @Valid @RequestBody CreateBlogRequest request) {
        HttpHeaders headers = currentResponse.getHeaders();
        String userId = headers.getFirst(KeyConstant.X_USER_ID);
        assert userId != null;
        request.setUserId(Integer.valueOf(userId));
        return processBaseResponse(blogService.createBlog(request));
    }

    @PutMapping("/{blogId}")
    public Mono<BaseResponse> updateBlog(ServerHttpResponse currentResponse, @Valid @RequestBody UpdateBlogRequest request, @PathVariable("blogId") Integer blogId) {
        HttpHeaders headers = currentResponse.getHeaders();
        String userId = headers.getFirst(KeyConstant.X_USER_ID);
        request.setId(blogId);
        assert userId != null;
        request.setUserId(Integer.valueOf(userId));
        blogService.updateBlog(request);
        return processBaseResponse();
    }

    @DeleteMapping("/{blogId}")
    public Mono<BaseResponse> deleteBlog(
            ServerHttpResponse currentResponse, @PathVariable("blogId") Integer blogId
    ) {
        HttpHeaders headers = currentResponse.getHeaders();
        String userId = headers.getFirst(KeyConstant.X_USER_ID);
        assert userId != null;
        this.blogService.deleteBlog(blogId);
        return processBaseResponse();
    }
}
