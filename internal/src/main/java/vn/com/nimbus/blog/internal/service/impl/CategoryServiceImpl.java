package vn.com.nimbus.blog.internal.service.impl;

import com.github.slugify.Slugify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vn.com.nimbus.blog.internal.service.CategoryService;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.BlogCategoryID;
import vn.com.nimbus.data.domain.Blogs;
import vn.com.nimbus.data.domain.Categories;
import vn.com.nimbus.data.repository.BlogCategoryRepository;
import vn.com.nimbus.data.repository.CategoryRepository;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.model.request.CreateBlogRequest;
import vn.com.nimbus.common.model.request.CreateCategoryRequest;
import vn.com.nimbus.common.model.request.UpdateCategoryRequest;
import vn.com.nimbus.common.model.response.CategoryResponse;

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
    public CategoryResponse getCategories() {
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
        blogCategoryRepository.deleteByCategoryId(opt.get().getId());
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

    @Override
    @Transactional
    public List<Categories> updateBlogCategories(Blogs blog, List<CreateBlogRequest.Category> categories) {
        if (categories == null)
            return null;

        Integer blogId = blog.getId();
        List<BlogCategory> linked = blogCategoryRepository.findByBlogId(blogId);
        List<Integer> linkedIds = linked.stream().map(l -> l.getId().getCategoryId()).collect(Collectors.toList());
        List<Integer> requestIds = categories.stream().map(CreateBlogRequest.Category::getId).collect(Collectors.toList());

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
