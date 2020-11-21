package vn.com.nimbus.blog.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.com.nimbus.blog.api.model.mapper.BlogMapper;
import vn.com.nimbus.blog.api.model.mapper.CategoryMapper;
import vn.com.nimbus.blog.api.model.mapper.TagMapper;
import vn.com.nimbus.blog.api.model.response.*;
import vn.com.nimbus.blog.api.service.BlogService;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.paging.Paging;
import vn.com.nimbus.data.domain.*;
import vn.com.nimbus.data.domain.constant.BlogStatus;
import vn.com.nimbus.data.domain.constant.PublicResponseType;
import vn.com.nimbus.data.repository.*;

import java.util.*;
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

    public BlogServiceImpl(
            BlogRepository blogRepository,
            CategoryRepository categoryRepository,
            BlogCategoryRepository blogCategoryRepository,
            BlogViewRepository blogViewRepository,
            BlogTagRepository blogTagRepository,
            TagRepository tagRepository,
            BlogAuthorRepository blogAuthorRepository,
            BlogContentRepository blogContentRepository,
            UserRepository userRepository
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
    }

    @Override
    public FeatureResponse getFeature() {
        final Pageable pageable = PageRequest.of(0, 5);
        FeatureResponse response = new FeatureResponse();

        Page<Category> page = categoryRepository.findAll(PageRequest.of(0, 10));
        List<Category> categories = page.getContent();

        List<Blog> mostViewBlogs = this.getMostViewedBlogs(Collections.emptyList());
        List<BlogResponse> highLights = this.extractBlogs(mostViewBlogs);
        response.setHighlights(highLights);

        List<Blog> allBlogs = new ArrayList<>(mostViewBlogs);
        List<Long> allBlogIds = mostViewBlogs.stream().map(Blog::getId).collect(Collectors.toList());
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

            List<Blog> blogs = blogRepository.findByIdIn(fetchIds);
            List<Blog> existBlogs = allBlogs.stream().filter(b -> existIds.contains(b.getId())).collect(Collectors.toList());

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
    public Paging<BasePublicResponse> searchBlog(String title, LimitOffsetPageable limitOffsetPageable) {
        Page<Blog> blogsPage = blogRepository.findByStatusAndTitleContains(BlogStatus.PUBLISHED, title, PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit()));

        List<BlogResponse> resBlogs = this.extractBlogs(blogsPage.getContent());

        limitOffsetPageable.setTotal(blogsPage.getTotalElements());

        BasePublicResponse response = new BasePublicResponse();
        response.setType(PublicResponseType.SEARCH);
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
    public List<CategoryResponse> getCategories() {
        List<Category> categories = categoryRepository.findAllByOrderByCreatedAt();
        return categories.stream().map(CategoryMapper.INSTANCE::toResponse).collect(Collectors.toList());
    }

    @Override
    public Object getBlog(String slug, LimitOffsetPageable limitOffsetPageable) {
        log.info("Build data from slug: {}", slug);

        Blog blog = blogRepository.findBySlugAndStatus(slug, BlogStatus.PUBLISHED);
        Paging<BasePublicResponse> res;
        if (blog != null) {
            res = this.buildBaseRes(PublicResponseType.BLOG, blog, limitOffsetPageable);
            new Thread(() -> {
                BlogView view = new BlogView();
                view.setBlogId(blog.getId());
                blogViewRepository.save(view);
            });
            return res;
        }

        Category category = categoryRepository.findBySlug(slug);
        if (category != null) {
            res = this.buildBaseRes(PublicResponseType.CATEGORY, category, limitOffsetPageable);
            return res;
        }

        Optional<Tag> tagOpt = tagRepository.findBySlug(slug);
        if (tagOpt.isPresent()) {
            Tag tag = tagOpt.get();
            res = this.buildBaseRes(PublicResponseType.TAG, tag, limitOffsetPageable);
            return res;
        }

        throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
    }

    private Paging<BasePublicResponse> buildBaseRes(PublicResponseType type, Object data, LimitOffsetPageable limitOffsetPageable) {
        log.info("Build base response from {} with data: {}", type, data);
        BasePublicResponse response = new BasePublicResponse();
        response.setType(type);

        List<Blog> blogs;

        switch (type) {
            case BLOG:
                Blog blog = (Blog) data;
                final Long blogId = blog.getId();

                BlogMapper mapper = BlogMapper.INSTANCE;
                BlogDetailResponse detail = mapper.toDetailResponse(blog);
                BlogContent content = blogContentRepository.findByBlogId(blogId);
                detail.setContent(content.getContent());

                List<BlogAuthor> authors = blogAuthorRepository.findById_BlogId(blogId);
                List<Long> authorIds = authors.stream()
                        .map(BlogAuthor::getId)
                        .map(BlogAuthorID::getAuthorId)
                        .collect(Collectors.toList());
                List<User> users = userRepository.findAllById(authorIds);
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
                if (!CollectionUtils.isEmpty(blogTags)) {
                    List<Long> tagIds = blogTags.stream()
                            .map(BlogTag::getId)
                            .map(BlogTagID::getTagId)
                            .collect(Collectors.toList());

                    List<Tag> tags = tagRepository.findAllById(tagIds);
                    TagMapper tagMapper = TagMapper.INSTANCE;
                    detail.setTags(tags.stream().map(tagMapper::toResponse).collect(Collectors.toList()));
                }
                response.setBlog(detail);

                blogs = Collections.singletonList(blog);
                break;
            case CATEGORY:
                Category category = (Category) data;
                CategoryMapper categoryMapper = CategoryMapper.INSTANCE;
                CategoryResponse categoryDetail = categoryMapper.toResponse(category);
                response.setCategory(categoryDetail);

                Page<BlogCategory> pageCat = blogCategoryRepository.findByCategoryId(
                        category.getId(),
                        BlogStatus.PUBLISHED,
                        PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit())
                );
                limitOffsetPageable.setTotal(pageCat.getTotalElements());

                List<Long> blogIds = pageCat.getContent()
                        .stream()
                        .map(BlogCategory::getId)
                        .map(BlogCategoryID::getBlogId)
                        .collect(Collectors.toList());

                blogs = blogRepository.findByIdIn(blogIds);
                break;
            case TAG:
                Tag tag = (Tag) data;
                TagMapper tagMapper = TagMapper.INSTANCE;
                TagResponse tagDetail = tagMapper.toResponse(tag);
                response.setTag(tagDetail);
                Page<BlogTag> pageTag = blogTagRepository.findByTag(
                        tag.getId(),
                        BlogStatus.PUBLISHED,
                        PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit())
                );
                limitOffsetPageable.setTotal(pageTag.getTotalElements());
                List<Long> relBlogIds = pageTag.getContent()
                        .stream()
                        .map(BlogTag::getId)
                        .map(BlogTagID::getBlogId)
                        .collect(Collectors.toList());

                blogs = blogRepository.findByIdIn(relBlogIds);
                break;
            default:
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (!type.equals(PublicResponseType.BLOG)) {
            List<BlogResponse> blogRes = this.extractBlogs(blogs);
            response.setBlogs(blogRes);
        }

        List<Long> excludeIds = blogs.stream().map(Blog::getId).collect(Collectors.toList());
        List<Blog> mostViewedBlogs = this.getMostViewedBlogs(excludeIds);
        response.setHighlights(this.extractBlogs(mostViewedBlogs));

        return new Paging<>(response, limitOffsetPageable);
    }

    private List<BlogResponse> extractBlogs(List<Blog> blogs) {
        BlogMapper mapper = BlogMapper.INSTANCE;
        List<Long> blogIds = blogs.stream().map(Blog::getId).collect(Collectors.toList());
        List<BlogAuthor> authors = blogAuthorRepository.findById_BlogIdIn(blogIds);
        List<Long> authorIds = authors.stream()
                .map(BlogAuthor::getId)
                .map(BlogAuthorID::getAuthorId)
                .collect(Collectors.toList());
        List<User> users = userRepository.findAllById(authorIds);

        return blogs.stream()
                .sorted(Comparator.comparing(Blog::getUpdatedAt))
                .map(blog -> {
                    BlogResponse res = mapper.toResponse(blog);

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
                .collect(Collectors.toList());
    }

    private List<Blog> getMostViewedBlogs(List<Long> excludeIds) {
        if (CollectionUtils.isEmpty(excludeIds)) {
            excludeIds = Collections.singletonList(-1L);
        }

        List<Long> page = blogViewRepository.getMostViews(excludeIds, PageRequest.of(0, 5));
        return blogRepository.findByIdIn(page);
    }

}
