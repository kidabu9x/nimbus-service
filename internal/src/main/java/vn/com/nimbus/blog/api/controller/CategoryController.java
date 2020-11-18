package vn.com.nimbus.blog.api.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import vn.com.nimbus.blog.api.model.response.CategoryResponse;
import vn.com.nimbus.blog.api.model.request.CategoryRequest;
import vn.com.nimbus.common.model.response.BaseResponse;
import vn.com.nimbus.blog.api.service.CategoryService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/categories")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @GetMapping
    public Mono<BaseResponse<List<CategoryResponse>>> getCategories() {
        return Mono.just(categoryService.getCategories()).map(BaseResponse::ofSucceeded);
    }

    @GetMapping("{id}")
    public Mono<BaseResponse<CategoryResponse>> getCategory(
            @PathVariable Integer id
    ) {
        return Mono.just(categoryService.getCategory(id)).map(BaseResponse::ofSucceeded);
    }

    @PostMapping
    public Mono<BaseResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {
        return Mono.just(categoryService.createCategory(request)).map(BaseResponse::ofSucceeded);
    }

    @PutMapping("{id}")
    public Mono<BaseResponse<CategoryResponse>> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequest request
    ) {
        request.setId(id);
        return Mono.just(categoryService.updateCategory(request)).map(BaseResponse::ofSucceeded);
    }

    @DeleteMapping("{id}")
    public Mono<BaseResponse<Boolean>> deleteCategory(
            @PathVariable Integer id
    ) {
        return Mono.just(categoryService.deleteCategory(id)).map(BaseResponse::ofSucceeded);
    }

}
