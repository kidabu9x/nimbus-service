package vn.com.nimbus.blog.internal.service;

import vn.com.nimbus.blog.internal.model.request.CategoryRequest;
import vn.com.nimbus.blog.internal.model.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategories();

    CategoryResponse getCategory(Long categoryId);

    CategoryResponse saveCategory(CategoryRequest request);

    boolean deleteCategory(Long id);
}
