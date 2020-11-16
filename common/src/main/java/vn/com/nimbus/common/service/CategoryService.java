package vn.com.nimbus.common.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.Blogs;
import vn.com.nimbus.data.domain.Categories;
import vn.com.nimbus.common.model.request.CreateBlogRequest;
import vn.com.nimbus.common.model.request.CreateCategoryRequest;
import vn.com.nimbus.common.model.request.UpdateCategoryRequest;
import vn.com.nimbus.common.model.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    Flux<CategoryResponse> getCategories();

    Mono<CategoryResponse> getCategory(Integer categoryId);

    Mono<CategoryResponse> createCategory(CreateCategoryRequest request);

    void updateCategory(UpdateCategoryRequest request);

    void deleteCategory(Integer id);

    List<Categories> updateBlogCategories(Blogs blog, List<CreateBlogRequest.Category> categories);

    void deleteRelation(List<BlogCategory> blogCategories);
}
