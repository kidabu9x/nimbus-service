package vn.com.nimbus.blog.api.service;

import vn.com.nimbus.blog.api.model.request.CategoryRequest;
import vn.com.nimbus.blog.api.model.response.CategoryResponse;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.domain.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategories();

    CategoryResponse getCategory(Integer categoryId);

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(CategoryRequest request);

    boolean deleteCategory(Integer id);

    List<Category> updateBlogCategories(Blog blog, List<Long> categories);

    void deleteRelation(List<BlogCategory> blogCategories);
}
