package vn.com.nimbus.blog.internal.service.impl;

import com.github.slugify.Slugify;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.nimbus.blog.internal.model.request.CategoryRequest;
import vn.com.nimbus.blog.internal.model.response.CategoryResponse;
import vn.com.nimbus.blog.internal.service.CategoryService;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.Category;
import vn.com.nimbus.data.repository.BlogCategoryRepository;
import vn.com.nimbus.data.repository.CategoryRepository;

import javax.annotation.Resource;
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
    public CategoryResponse getCategory(Long categoryId) {
        Optional<Category> opt = categoryRepository.findById(categoryId);
        if (opt.isEmpty()) {
            log.warn("Category not found, id : {}", categoryId);
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        return this.buildResponse(opt.get());
    }

    @Override
    @Transactional
    public CategoryResponse saveCategory(CategoryRequest request) {
        Category category = new Category();
        if (request.getId() != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(request.getId());
            if (categoryOpt.isEmpty()) {
                throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            category = categoryOpt.get();
        }

        if (StringUtils.isEmpty(category.getTitle()) || !category.getTitle().equals(request.getTitle())) {
            Category existCat = categoryRepository.findByTitle(request.getTitle());
            if (existCat != null) {
                throw new BaseException(ErrorCode.RESOURCE_CONFLICT);
            }
        }

        String slug = slugify.slugify(request.getTitle());
        Long duplicate = categoryRepository.countBySlugContains(slug);
        if (duplicate > 0) {
            slug = slug.concat("-").concat(duplicate.toString());
        }

        category = new Category();
        category.setTitle(request.getTitle());
        category.setSlug(slug);
        category = categoryRepository.save(category);

        return this.buildResponse(category);
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        Optional<Category> opt = categoryRepository.findById(id);
        if (opt.isEmpty()) {
            log.warn("Category not found, id : {}", id);
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        List<BlogCategory> categories = blogCategoryRepository.findById_CategoryId(id);
        blogCategoryRepository.deleteAll(categories);
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
}
