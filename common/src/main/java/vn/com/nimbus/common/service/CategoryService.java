package vn.com.nimbus.common.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.request.CreateCategoryRequest;
import vn.com.nimbus.common.model.request.UpdateCategoryRequest;
import vn.com.nimbus.common.model.response.CategoryResponse;

public interface CategoryService {
    Flux<CategoryResponse> getCategories();

    Mono<CategoryResponse> getCategory(Integer categoryId);

    Mono<CategoryResponse> createCategory(CreateCategoryRequest request);

    void updateCategory(UpdateCategoryRequest request);

    void deleteCategory(Integer id);

}
