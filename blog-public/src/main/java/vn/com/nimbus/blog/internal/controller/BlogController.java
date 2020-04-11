package vn.com.nimbus.blog.internal.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.controller.AbstractController;
import vn.com.nimbus.common.model.constant.KeyConstant;
import vn.com.nimbus.common.model.request.CreateBlogRequest;
import vn.com.nimbus.common.model.request.UpdateBlogRequest;
import vn.com.nimbus.common.model.response.BaseResponse;
import vn.com.nimbus.common.service.BlogPublicService;
import vn.com.nimbus.common.service.BlogService;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/")
public class BlogController extends AbstractController {
    @Resource
    private BlogPublicService blogService;

    @GetMapping("/{slug}")
    public Mono<BaseResponse> getBlog(@PathVariable String slug) {
        return processBaseResponse(blogService.getBlog(slug));
    }

//    @GetMapping("/{blogId}")
//    public Mono<BaseResponse> getBlog(@PathVariable Integer blogId) {
//        return processBaseResponse(blogService.getBlog(blogId));
//    }

}
