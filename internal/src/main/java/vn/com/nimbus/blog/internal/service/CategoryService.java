package vn.com.nimbus.blog.internal.service;

import vn.com.nimbus.blog.internal.model.request.CreateBlogRequest;
import vn.com.nimbus.blog.internal.model.request.CreateCategoryRequest;
import vn.com.nimbus.blog.internal.model.request.UpdateCategoryRequest;
import vn.com.nimbus.blog.internal.model.response.CategoryResponse;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.Blogs;
import vn.com.nimbus.data.domain.Categories;

import java.util.List;

public interface CategoryService {
    CategoryResponse getCategories();

    CategoryResponse getCategory(Integer categoryId);

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(UpdateCategoryRequest request);

    boolean deleteCategory(Integer id);

    List<Categories> updateBlogCategories(Blogs blog, List<CreateBlogRequest.Category> categories);

    void deleteRelation(List<BlogCategory> blogCategories);
}
