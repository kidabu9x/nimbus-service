package vn.com.nimbus.blog.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import vn.com.nimbus.blog.api.model.response.BasePublicResponse;
import vn.com.nimbus.blog.api.model.response.CategoryResponse;
import vn.com.nimbus.blog.api.model.response.FeatureResponse;
import vn.com.nimbus.blog.api.service.BlogPublicService;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/v1/")
public class BlogController {
    private final BlogPublicService blogService;

    @Autowired
    public BlogController(BlogPublicService blogService) {
        this.blogService = blogService;
    }


    @GetMapping("/features")
    public Mono<BaseResponse<FeatureResponse>> getFeature() {
        return Mono
                .just(blogService.getFeature())
                .map(BaseResponse::ofSucceeded);
    }

    @GetMapping("/search")
    public Mono<BaseResponse<List<BasePublicResponse>>> searchBlog(
            @RequestParam(name = "query", required = false, defaultValue = "") String search,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset
    ) {
        LimitOffsetPageable limitOffsetPageable = new LimitOffsetPageable();
        limitOffsetPageable.setLimit(limit);
        limitOffsetPageable.setOffset(offset);
        return Mono
                .just(blogService.searchBlog(search, limitOffsetPageable))
                .map(BaseResponse::ofSucceeded);
    }

    @GetMapping("/categories")
    public Mono<BaseResponse<List<CategoryResponse>>> getCategories() {
        return Mono
                .just(blogService.getCategories())
                .map(BaseResponse::ofSucceeded);
    }

    @GetMapping("/{slug}")
    public Mono<BaseResponse<Object>> getBySlug(
            @PathVariable String slug,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset
    ) {
        LimitOffsetPageable limitOffsetPageable = new LimitOffsetPageable();
        limitOffsetPageable.setLimit(limit);
        limitOffsetPageable.setOffset(offset);
        return Mono
                .just(blogService.getBlog(slug, limitOffsetPageable))
                .map(BaseResponse::ofSucceeded);
    }

}
