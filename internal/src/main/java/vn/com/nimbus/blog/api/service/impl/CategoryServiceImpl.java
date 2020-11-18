package vn.com.nimbus.blog.api.service.impl;

import com.github.slugify.Slugify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.nimbus.blog.api.model.request.BlogRequest;
import vn.com.nimbus.blog.api.model.request.CategoryRequest;
import vn.com.nimbus.blog.api.model.response.CategoryResponse;
import vn.com.nimbus.blog.api.service.CategoryService;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.BlogCategoryID;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.domain.Category;
import vn.com.nimbus.data.repository.BlogCategoryRepository;
import vn.com.nimbus.data.repository.CategoryRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryRepository categoryRepository;

    @Resource
    private BlogCategoryRepository blogCategoryRepository;

    private final Slugify slugify = new Slugify();

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return categoryRepository
                .findAllByOrderByCreatedAt()
                .stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategory(Integer categoryId) {
        Optional<Category> opt = categoryRepository.findById(categoryId);
        if (opt.isEmpty()) {
            log.warn("Category not found, id : {}", categoryId);
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        return this.buildResponse(opt.get());
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String title = request.getTitle().trim();

        Category category = categoryRepository.findByTitle(title);
        if (category != null) {
            throw new BaseException(ErrorCode.RESOURCE_CONFLICT);
        }

        String slug = slugify.slugify(title);
        Long duplicate = categoryRepository.countBySlugContains(slug);
        if (duplicate > 0)
            slug = slug.concat("-").concat(duplicate.toString());

        category = new Category();
        category.setTitle(title);
        category.setSlug(slug);
        category = categoryRepository.save(category);

        return this.buildResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UpdateCategoryRequest request) {
        Integer id = request.getId();
        String title = request.getTitle().trim();
        Optional<Category> opt = categoryRepository.findById(id);
        if (opt.isEmpty()) {
            log.warn("Category not found, id : {}", id);
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        Category category = opt.get();
        category.setTitle(title);
        return this.buildResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public boolean deleteCategory(Integer id) {
        Optional<Category> opt = categoryRepository.findById(id);
        if (opt.isEmpty()) {
            log.warn("Category not found, id : {}", id);
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        blogCategoryRepository.deleteByCategoryId(opt.get().getId());
        categoryRepository.delete(opt.get());
        return true;
    }

    private CategoryResponse buildResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setSlug(category.getSlug());
        response.setTitle(category.getTitle());
        response.setTotalBlogs(0L);
        return response;
    }

    @Override
    @Transactional
    public List<Category> updateBlogCategories(Blog blog, List<BlogRequest.Category> categories) {
        if (categories == null)
            return null;

        Integer blogId = blog.getId();
        List<BlogCategory> linked = blogCategoryRepository.findByBlogId(blogId);
        List<Integer> linkedIds = linked.stream().map(l -> l.getId().getCategoryId()).collect(Collectors.toList());
        List<Integer> requestIds = categories.stream().map(BlogRequest.Category::getId).collect(Collectors.toList());

        List<BlogCategory> newLinked = requestIds.stream().filter(t -> !linkedIds.contains(t))
                .map(t -> {
                    BlogCategoryID id = new BlogCategoryID();
                    id.setBlogId(blogId);
                    id.setCategoryId(t);
                    BlogCategory link = new BlogCategory();
                    link.setId(id);
                    return link;
                })
                .collect(Collectors.toList());
        blogCategoryRepository.saveAll(newLinked);

        List<BlogCategory> oldLinked = linkedIds.stream().filter(t -> !requestIds.contains(t))
                .map(t -> {
                    BlogCategoryID id = new BlogCategoryID();
                    id.setBlogId(blogId);
                    id.setCategoryId(t);
                    BlogCategory link = new BlogCategory();
                    link.setId(id);
                    return link;
                })
                .collect(Collectors.toList());
        blogCategoryRepository.deleteAll(oldLinked);


        return new ArrayList<>();
    }

    @Override
    public void deleteRelation(List<BlogCategory> blogCategories) {
        blogCategoryRepository.deleteAll(blogCategories);
    }
}
