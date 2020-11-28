package vn.com.nimbus.blog.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import vn.com.nimbus.blog.api.model.mapper.BlogMapper;
import vn.com.nimbus.blog.api.model.mapper.CategoryMapper;
import vn.com.nimbus.blog.api.model.mapper.HachiumCourseMapper;
import vn.com.nimbus.blog.api.model.mapper.TagMapper;
import vn.com.nimbus.blog.api.model.response.BlogDetailResponse;
import vn.com.nimbus.blog.api.model.response.BlogResponse;
import vn.com.nimbus.blog.api.model.response.CategoryDetailResponse;
import vn.com.nimbus.blog.api.model.response.CategoryResponse;
import vn.com.nimbus.blog.api.model.response.FeatureResponse;
import vn.com.nimbus.blog.api.model.response.HachiumCourseResponse;
import vn.com.nimbus.blog.api.model.response.SearchResponse;
import vn.com.nimbus.blog.api.model.response.TagDetailResponse;
import vn.com.nimbus.blog.api.model.response.TagResponse;
import vn.com.nimbus.blog.api.service.BlogService;
import vn.com.nimbus.common.model.dto.SlugPoolDto;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.paging.Paging;
import vn.com.nimbus.common.service.SlugPoolService;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.domain.BlogAuthor;
import vn.com.nimbus.data.domain.BlogAuthorID;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.BlogCategoryID;
import vn.com.nimbus.data.domain.BlogContent;
import vn.com.nimbus.data.domain.BlogTag;
import vn.com.nimbus.data.domain.BlogTagID;
import vn.com.nimbus.data.domain.Category;
import vn.com.nimbus.data.domain.HachiumCategoryMapping;
import vn.com.nimbus.data.domain.HachiumCategoryMappingID;
import vn.com.nimbus.data.domain.HachiumCourse;
import vn.com.nimbus.data.domain.Tag;
import vn.com.nimbus.data.domain.User;
import vn.com.nimbus.data.domain.constant.BlogStatus;
import vn.com.nimbus.data.domain.constant.SlugPoolType;
import vn.com.nimbus.data.dto.output.BlogDto;
import vn.com.nimbus.data.dto.output.CategoryDto;
import vn.com.nimbus.data.repository.BlogAuthorRepository;
import vn.com.nimbus.data.repository.BlogCategoryRepository;
import vn.com.nimbus.data.repository.BlogContentRepository;
import vn.com.nimbus.data.repository.BlogRepository;
import vn.com.nimbus.data.repository.BlogTagRepository;
import vn.com.nimbus.data.repository.BlogViewRepository;
import vn.com.nimbus.data.repository.CategoryRepository;
import vn.com.nimbus.data.repository.HachiumCategoryMappingRepository;
import vn.com.nimbus.data.repository.HachiumCourseRepository;
import vn.com.nimbus.data.repository.TagRepository;
import vn.com.nimbus.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogViewRepository blogViewRepository;
    private final BlogTagRepository blogTagRepository;
    private final TagRepository tagRepository;
    private final BlogAuthorRepository blogAuthorRepository;
    private final BlogContentRepository blogContentRepository;
    private final UserRepository userRepository;
    private final SlugPoolService slugPoolService;
    private final HachiumCategoryMappingRepository hachiumCategoryMappingRepository;
    private final HachiumCourseRepository hachiumCourseRepository;

    @Autowired
    public BlogServiceImpl(
            BlogRepository blogRepository,
            CategoryRepository categoryRepository,
            BlogCategoryRepository blogCategoryRepository,
            BlogViewRepository blogViewRepository,
            BlogTagRepository blogTagRepository,
            TagRepository tagRepository,
            BlogAuthorRepository blogAuthorRepository,
            BlogContentRepository blogContentRepository,
            UserRepository userRepository,
            SlugPoolService slugPoolService,
            HachiumCategoryMappingRepository hachiumCategoryMappingRepository,
            HachiumCourseRepository hachiumCourseRepository
    ) {
        this.blogRepository = blogRepository;
        this.categoryRepository = categoryRepository;
        this.blogCategoryRepository = blogCategoryRepository;
        this.blogViewRepository = blogViewRepository;
        this.blogTagRepository = blogTagRepository;
        this.tagRepository = tagRepository;
        this.blogAuthorRepository = blogAuthorRepository;
        this.blogContentRepository = blogContentRepository;
        this.userRepository = userRepository;
        this.slugPoolService = slugPoolService;
        this.hachiumCategoryMappingRepository = hachiumCategoryMappingRepository;
        this.hachiumCourseRepository = hachiumCourseRepository;
    }

    @Override
    public Paging<Object> getBySlug(String slug, LimitOffsetPageable limitOffsetPageable) {
        if (StringUtils.isEmpty(slug)) {
            throw new BaseException(ErrorCode.INVALID_PARAMETERS);
        }
        SlugPoolDto slugPoolDto = slugPoolService.get(slug);
        SlugPoolType type = slugPoolDto.getType();
        if (type.equals(SlugPoolType.BLOG)) {
            Optional<Blog> blogOpt = blogRepository.findById(slugPoolDto.getTargetId());
            if (blogOpt.isEmpty() || !blogOpt.get().getStatus().equals(BlogStatus.PUBLISHED)) {
                throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            final Blog blog = blogOpt.get();
            final Long blogId = blog.getId();

            BlogMapper mapper = BlogMapper.INSTANCE;
            BlogDetailResponse detail = mapper.toDetailResponse(blog);
            BlogContent content = blogContentRepository.findByBlogId(blogId);
            detail.setContent(content.getContent());

            List<BlogAuthor> authors = blogAuthorRepository.findById_BlogId(blogId);
            List<User> users = this.getAuthors(authors);
            List<BlogResponse.Author> resAuthors = users.stream().map(mapper::toAuthorResponse).collect(Collectors.toList());
            detail.setAuthors(resAuthors);

            List<BlogCategory> blogCategories = blogCategoryRepository.findById_BlogId(blogId);
            if (!CollectionUtils.isEmpty(blogCategories)) {
                List<Long> categoryIds = blogCategories.stream()
                        .map(BlogCategory::getId)
                        .map(BlogCategoryID::getCategoryId)
                        .collect(Collectors.toList());

                List<Category> categories = categoryRepository.findAllById(categoryIds);
                CategoryMapper categoryMapper = CategoryMapper.INSTANCE;
                detail.setCategories(categories.stream().map(categoryMapper::toResponse).collect(Collectors.toList()));
            }

            List<BlogTag> blogTags = blogTagRepository.findById_BlogId(blogId);
            detail.setTags(new ArrayList<>());
            if (!CollectionUtils.isEmpty(blogTags)) {
                List<Long> tagIds = blogTags.stream()
                        .map(BlogTag::getId)
                        .map(BlogTagID::getTagId)
                        .collect(Collectors.toList());

                List<Tag> tags = tagRepository.findAllById(tagIds);
                TagMapper tagMapper = TagMapper.INSTANCE;
                detail.setTags(tags.stream().map(tagMapper::toResponse).collect(Collectors.toList()));
            }

            List<BlogDto> mostViewedBlogs = this.getMostViewedBlogs(List.of(blog.getId()));
            detail.setHighlights(this.extractBlogs(mostViewedBlogs));

            limitOffsetPageable.setTotal(0L);
            return new Paging<>(detail, limitOffsetPageable);
        } else {
            Optional<Category> categoryOpt = categoryRepository.findById(slugPoolDto.getTargetId());
            if (categoryOpt.isEmpty()) {
                throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            final Category category = categoryOpt.get();
            CategoryResponse catRes = CategoryMapper.INSTANCE.toResponse(category);
            CategoryDetailResponse response = new CategoryDetailResponse();
            response.setCategory(catRes);

            Page<BlogCategory> pageCat = blogCategoryRepository.findByCategoryId(
                    category.getId(),
                    BlogStatus.PUBLISHED,
                    PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit())
            );
            List<Long> blogIds = pageCat.getContent()
                    .stream()
                    .map(BlogCategory::getId)
                    .map(BlogCategoryID::getBlogId)
                    .collect(Collectors.toList());
            List<BlogDto> blogs = blogRepository.findDtoByIds(blogIds);
            response.setBlogs(this.extractBlogs(blogs));
            List<BlogDto> mostViewedBlogs = this.getMostViewedBlogs(blogIds);
            response.setHighlights(this.extractBlogs(mostViewedBlogs));
            limitOffsetPageable.setTotal(pageCat.getTotalElements());
            return new Paging<>(response, limitOffsetPageable);
        }
    }

    @Override
    public Paging<TagDetailResponse> getTag(String slug, LimitOffsetPageable limitOffsetPageable) {
        if (StringUtils.isEmpty(slug)) {
            throw new BaseException(ErrorCode.INVALID_PARAMETERS);
        }
        Tag tag = tagRepository.findBySlug(slug);
        if (tag == null) {
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        TagResponse response = new TagResponse();
        response.setId(tag.getId());
        response.setSlug(tag.getSlug());
        response.setTitle(tag.getTitle());

        TagDetailResponse detail = new TagDetailResponse();
        detail.setTag(response);
        Page<BlogTag> pageCat = blogTagRepository.findByTag(
                tag.getId(),
                BlogStatus.PUBLISHED,
                PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit())
        );
        limitOffsetPageable.setTotal(pageCat.getTotalElements());
        List<Long> blogIds = pageCat.getContent()
                .stream()
                .map(BlogTag::getId)
                .map(BlogTagID::getBlogId)
                .collect(Collectors.toList());
        List<BlogDto> blogs = blogRepository.findDtoByIds(blogIds);
        detail.setBlogs(this.extractBlogs(blogs));
        List<BlogDto> mostViewedBlogs = this.getMostViewedBlogs(blogIds);
        detail.setHighlights(this.extractBlogs(mostViewedBlogs));
        return new Paging<>(detail, limitOffsetPageable);
    }

    @Override
    public Paging<SearchResponse> searchBlog(String title, LimitOffsetPageable limitOffsetPageable) {
        Page<BlogDto> blogsPage = blogRepository.findByStatusAndTitleContains(
                BlogStatus.PUBLISHED,
                title,
                PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit())
        );

        List<BlogResponse> resBlogs = this.extractBlogs(blogsPage.getContent());

        limitOffsetPageable.setTotal(blogsPage.getTotalElements());

        SearchResponse response = new SearchResponse();
        response.setBlogs(resBlogs);
        response.setHighlights(this.extractBlogs(
                this.getMostViewedBlogs(
                        resBlogs.stream()
                                .map(BlogResponse::getId)
                                .collect(Collectors.toList())
                )
                )
        );
        return new Paging<>(response, limitOffsetPageable);
    }

    @Override
    public FeatureResponse getFeature() {
        final Pageable pageable = PageRequest.of(0, 5);
        FeatureResponse response = new FeatureResponse();

        Page<Category> page = categoryRepository.findAll(PageRequest.of(0, 10));
        List<Category> categories = page.getContent();

        List<BlogDto> mostViewBlogs = this.getMostViewedBlogs(Collections.emptyList());
        List<BlogResponse> highLights = this.extractBlogs(mostViewBlogs);
        response.setHighlights(highLights);

        List<BlogDto> allBlogs = new ArrayList<>(mostViewBlogs);
        List<Long> allBlogIds = mostViewBlogs.stream()
                .map(BlogDto::getBlog)
                .map(Blog::getId)
                .collect(Collectors.toList());
        List<FeatureResponse.Feature> features = new ArrayList<>();
        categories.forEach(category -> {
            List<Long> ids = blogViewRepository.getMostViewsByCategoryId(category.getId(), pageable);
            List<Long> fetchIds = new ArrayList<>();
            List<Long> existIds = new ArrayList<>();

            ids.forEach(i -> {
                if (allBlogIds.contains(i)) {
                    existIds.add(i);
                } else {
                    fetchIds.add(i);
                }
            });

            List<BlogDto> blogs = blogRepository.findDtoByIds(fetchIds);
            List<BlogDto> existBlogs = allBlogs.stream().filter(b -> existIds.contains(b.getBlog().getId())).collect(Collectors.toList());

            allBlogIds.addAll(fetchIds);
            allBlogs.addAll(blogs);

            blogs.addAll(existBlogs);

            FeatureResponse.Feature feature = new FeatureResponse.Feature();
            feature.setBlogs(this.extractBlogs(blogs));
            feature.setCategory(CategoryMapper.INSTANCE.toResponse(category));
            features.add(feature);
        });
        response.setFeatures(features);
        return response;
    }

    @Override
    public List<CategoryResponse> getCategories() {
        List<CategoryDto> categories = categoryRepository.findAllDto();
        return categories.stream().map(dto -> {
            CategoryResponse response = CategoryMapper.INSTANCE.toResponse(dto.getCategory());
            response.setSlug(dto.getSlug());
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public List<HachiumCourseResponse> getCourses(String slug) {
        if (!StringUtils.isEmpty(slug)) {
            SlugPoolDto slugPoolDto = slugPoolService.get(slug);
            SlugPoolType type = slugPoolDto.getType();
            if (type.equals(SlugPoolType.BLOG)) {
                Optional<Blog> blogOpt = blogRepository.findById(slugPoolDto.getTargetId());
                if (blogOpt.isEmpty() || !blogOpt.get().getStatus().equals(BlogStatus.PUBLISHED)) {
                    throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
                }
                final Blog blog = blogOpt.get();
                List<BlogCategory> blogCategories = blogCategoryRepository.findById_BlogId(blog.getId());
                if (!CollectionUtils.isEmpty(blogCategories)) {
                    List<Long> categoryIds = blogCategories.stream().map(BlogCategory::getId).map(BlogCategoryID::getCategoryId).collect(Collectors.toList());
                    List<HachiumCategoryMapping> mappings = hachiumCategoryMappingRepository.findById_CategoryIdIn(categoryIds);
                    if (!CollectionUtils.isEmpty(mappings)) {
                        return this.getHachiumCourseResponses(mappings);
                    }
                }
            } else {
                Optional<Category> categoryOpt = categoryRepository.findById(slugPoolDto.getTargetId());
                if (categoryOpt.isEmpty()) {
                    throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
                }
                final Category category = categoryOpt.get();
                List<HachiumCategoryMapping> mappings = hachiumCategoryMappingRepository.findById_CategoryId(category.getId());
                if (!CollectionUtils.isEmpty(mappings)) {
                    return this.getHachiumCourseResponses(mappings);
                }
            }
        }

        Page<HachiumCourse> coursePage = hachiumCourseRepository.findAll(PageRequest.of(0, 5));
        return coursePage
                .get()
                .map(HachiumCourseMapper.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    private List<HachiumCourseResponse> getHachiumCourseResponses(List<HachiumCategoryMapping> mappings) {
        if (!CollectionUtils.isEmpty(mappings)) {
            List<Long> hachiumCategoryIds = mappings.stream().map(HachiumCategoryMapping::getId).map(HachiumCategoryMappingID::getHachiumCategoryId).collect(Collectors.toList());
            List<HachiumCourse> courses = hachiumCourseRepository.findByHachiumCategoryIdIn(hachiumCategoryIds);
            return courses
                    .subList(0, Math.min(courses.size(), 5))
                    .stream()
                    .map(HachiumCourseMapper.INSTANCE::toResponse)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private List<BlogResponse> extractBlogs(List<BlogDto> blogs) {
        BlogMapper mapper = BlogMapper.INSTANCE;
        List<Long> blogIds = blogs.stream().map(BlogDto::getBlog).map(Blog::getId).collect(Collectors.toList());
        List<BlogAuthor> authors = blogAuthorRepository.findById_BlogIdIn(blogIds);
        List<User> users = this.getAuthors(authors);

        return blogs.stream()
                .map(blogDto -> {
                    Blog blog = blogDto.getBlog();
                    BlogResponse res = mapper.toResponse(blog);
                    res.setSlug(blogDto.getSlug());

                    List<Long> blogAuthorIds = authors.stream()
                            .filter(blogAuthor -> blogAuthor.getId().getBlogId().equals(blog.getId()))
                            .map(BlogAuthor::getId)
                            .map(BlogAuthorID::getAuthorId)
                            .collect(Collectors.toList());

                    List<User> blogAuthors = users.stream()
                            .filter(user -> blogAuthorIds.contains(user.getId()))
                            .collect(Collectors.toList());

                    List<BlogResponse.Author> resAuthors = blogAuthors.stream().map(mapper::toAuthorResponse).collect(Collectors.toList());
                    res.setAuthors(resAuthors);
                    return res;
                })
                .sorted(Comparator.comparing(BlogResponse::getUpdatedAt))
                .collect(Collectors.toList());
    }

    private List<User> getAuthors(List<BlogAuthor> authors) {
        List<Long> authorIds = authors.stream()
                .map(BlogAuthor::getId)
                .map(BlogAuthorID::getAuthorId)
                .collect(Collectors.toList());
        return userRepository.findAllById(authorIds);
    }

    private List<BlogDto> getMostViewedBlogs(List<Long> excludeIds) {
        if (CollectionUtils.isEmpty(excludeIds)) {
            excludeIds = Collections.singletonList(-1L);
        }

        List<Long> page = blogViewRepository.getMostViews(excludeIds, PageRequest.of(0, 5));
        return blogRepository.findDtoByIds(page);
    }

}
