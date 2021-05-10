package vn.com.nimbus.blog.internal.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.nimbus.blog.internal.model.request.CategoryRequest;
import vn.com.nimbus.blog.internal.model.response.CategoryResponse;
import vn.com.nimbus.blog.internal.service.CategoryService;
import vn.com.nimbus.common.model.dto.SlugPoolDto;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.common.service.SlugPoolService;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.Category;
import vn.com.nimbus.data.domain.HachiumCategory;
import vn.com.nimbus.data.domain.HachiumCategoryMapping;
import vn.com.nimbus.data.domain.HachiumCategoryMappingID;
import vn.com.nimbus.data.domain.constant.SlugPoolType;
import vn.com.nimbus.data.repository.BlogCategoryRepository;
import vn.com.nimbus.data.repository.CategoryRepository;
import vn.com.nimbus.data.repository.HachiumCategoryMappingRepository;
import vn.com.nimbus.data.repository.HachiumCategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final SlugPoolService slugPoolService;
    private final HachiumCategoryRepository hachiumCategoryRepository;
    private final HachiumCategoryMappingRepository hachiumCategoryMappingRepository;

    @Autowired
    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            BlogCategoryRepository blogCategoryRepository,
            SlugPoolService slugPoolService,
            HachiumCategoryRepository hachiumCategoryRepository,
            HachiumCategoryMappingRepository hachiumCategoryMappingRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.blogCategoryRepository = blogCategoryRepository;
        this.slugPoolService = slugPoolService;
        this.hachiumCategoryRepository = hachiumCategoryRepository;
        this.hachiumCategoryMappingRepository = hachiumCategoryMappingRepository;
    }

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

        category = new Category();
        category.setTitle(request.getTitle());
        category = categoryRepository.save(category);

        SlugPoolDto slugPoolDto = new SlugPoolDto();
        slugPoolDto.setTargetId(category.getId());
        slugPoolDto.setTitle(category.getTitle());
        slugPoolDto.setType(SlugPoolType.CATEGORY);
        slugPoolService.save(slugPoolDto);

        if (request.getHachiumCategoryId() != null) {
            final Long hachiumCategoryId = request.getHachiumCategoryId();
            final Long categoryId = category.getId();
            Optional<HachiumCategory> hachiumCategoryOpt = hachiumCategoryRepository.findById(hachiumCategoryId);
            if (hachiumCategoryOpt.isEmpty()) {
                throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "Not found hachium category");
            }
            List<HachiumCategoryMapping> mappings = hachiumCategoryMappingRepository.findById_CategoryId(categoryId);
            HachiumCategoryMappingID mappingID = new HachiumCategoryMappingID(categoryId, hachiumCategoryId);
            Optional<HachiumCategoryMapping> mappingOpt = mappings.stream().filter(m -> m.getId().getHachiumCategoryId().equals(hachiumCategoryId)).findAny();
            if (mappingOpt.isEmpty()) {
                hachiumCategoryMappingRepository.save(new HachiumCategoryMapping(mappingID));
            }
        }

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

        SlugPoolDto slugPoolDto = new SlugPoolDto();
        slugPoolDto.setTargetId(id);
        slugPoolDto.setType(SlugPoolType.CATEGORY);
        slugPoolService.delete(slugPoolDto);

        categoryRepository.delete(opt.get());
        return true;
    }

    private CategoryResponse buildResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setTitle(category.getTitle());
        response.setTotalBlogs(0L);
        return response;
    }
}
