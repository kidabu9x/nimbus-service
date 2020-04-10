package vn.com.nimbus.common.service;

import com.github.slugify.Slugify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.data.domain.Categories;
import vn.com.nimbus.common.data.repository.CategoryRepository;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.model.request.CreateCategoryRequest;
import vn.com.nimbus.common.model.request.UpdateCategoryRequest;
import vn.com.nimbus.common.model.response.CategoryResponse;

import javax.annotation.Resource;
import java.util.Optional;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryRepository categoryRepository;

    private final Slugify slugify = new Slugify();

    @Override
    @Transactional(readOnly = true)
    public Flux<CategoryResponse> getCategories() {
        return Flux.fromStream(categoryRepository.findAllByOrderByCreatedAt().stream().map(this::buildResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CategoryResponse> getCategory(Integer categoryId) {
        Optional<Categories> opt = categoryRepository.findById(categoryId);
        if (!opt.isPresent()) {
            log.warn("Category not found, id : {}", categoryId);
            throw new AppException(AppExceptionCode.CATEGORY_NOT_FOUND);
        }

        return Mono.just(this.buildResponse(opt.get()));
    }

    @Override
    @Transactional
    public Mono<CategoryResponse> createCategory(CreateCategoryRequest request) {
        String title = request.getTitle().trim();

        Categories category = categoryRepository.findByTitle(title);
        if (category != null)
            throw new AppException(AppExceptionCode.CATEGORY_HAS_EXISTS);

        String slug = slugify.slugify(title);
        Long duplicate = categoryRepository.countBySlugContains(slug);
        if (duplicate > 0)
            slug = slug.concat("-").concat(duplicate.toString());

        category = new Categories();
        category.setTitle(title);
        category.setSlug(slug);
        category = categoryRepository.save(category);

        return Mono.just(this.buildResponse(category));
    }

    @Override
    @Transactional
    public void updateCategory(UpdateCategoryRequest request) {
        Integer id = request.getId();
        String title = request.getTitle().trim();

        Optional<Categories> opt = categoryRepository.findById(id);
        if (!opt.isPresent()) {
            log.warn("Category not found, id : {}", id);
            throw new AppException(AppExceptionCode.CATEGORY_NOT_FOUND);
        }

        Categories category = opt.get();
        category.setTitle(title);

        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        Optional<Categories> opt = categoryRepository.findById(id);
        if (!opt.isPresent()) {
            log.warn("Category not found, id : {}", id);
            throw new AppException(AppExceptionCode.CATEGORY_NOT_FOUND);
        }

        categoryRepository.delete(opt.get());
    }

    private CategoryResponse buildResponse(Categories category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setSlug(category.getSlug());
        response.setTitle(category.getTitle());
        response.setTotalBlogs(0L);
        return response;
    }
}
