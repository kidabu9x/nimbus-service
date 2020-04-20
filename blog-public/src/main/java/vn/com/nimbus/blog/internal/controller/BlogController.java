package vn.com.nimbus.blog.internal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.controller.AbstractController;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.response.BaseResponse;
import vn.com.nimbus.common.service.BlogPublicService;
import vn.com.nimbus.common.service.CategoryService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/v1/")
public class BlogController extends AbstractController {
    @Resource
    private BlogPublicService blogService;

    @Resource
    private CategoryService categoryService;

    @GetMapping("/feature")
    public Mono<BaseResponse> getFeature() {
        return processBaseResponse(blogService.getFeature());
    }

    @GetMapping("/categories")
    public Mono<BaseResponse> getCategories() {
        return processBaseResponse(categoryService.getCategories().collectList());
    }

    @GetMapping("/{slug}")
    public Mono<BaseResponse> getBySlug(
            @PathVariable String slug,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset
    ) {
        LimitOffsetPageable limitOffsetPageable = new LimitOffsetPageable();
        limitOffsetPageable.setLimit(limit);
        limitOffsetPageable.setOffset(offset);
        return processBaseResponse(blogService.getBlog(slug, limitOffsetPageable));
    }

}
