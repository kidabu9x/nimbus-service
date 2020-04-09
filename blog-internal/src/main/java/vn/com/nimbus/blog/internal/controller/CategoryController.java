package vn.com.nimbus.blog.internal.controller;

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
import vn.com.nimbus.common.model.request.CreateCategoryRequest;
import vn.com.nimbus.common.model.request.UpdateCategoryRequest;
import vn.com.nimbus.common.model.response.BaseResponse;
import vn.com.nimbus.common.service.CategoryService;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/categories")
public class CategoryController extends AbstractController {
    @Resource
    private CategoryService categoryService;

    @GetMapping
    public Mono<BaseResponse> getCategories() {
        return processBaseResponse(categoryService.getCategories().collectList());
    }

    @GetMapping("{id}")
    public Mono<BaseResponse> getCategory(@PathVariable Integer id) {
        return processBaseResponse(categoryService.getCategory(id));
    }

    @PostMapping
    public Mono<BaseResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return processBaseResponse(categoryService.createCategory(request));
    }

    @PutMapping("{id}")
    public Mono<BaseResponse> updateCategory(@PathVariable Integer id, @Valid @RequestBody UpdateCategoryRequest request) {
        request.setId(id);
        categoryService.updateCategory(request);
        return processBaseResponse();
    }

    @DeleteMapping("{id}")
    public Mono<BaseResponse> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return processBaseResponse();
    }

}
