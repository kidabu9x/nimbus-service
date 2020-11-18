package vn.com.nimbus.blog.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import vn.com.nimbus.blog.api.service.BlogPublicService;
import vn.com.nimbus.blog.api.service.BlogViewService;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.paging.Paging;
import vn.com.nimbus.common.model.response.BasePublicResponse;
import vn.com.nimbus.common.model.response.BlogPublicDetailResponse;
import vn.com.nimbus.common.model.response.FeatureResponse;
import vn.com.nimbus.common.utils.FormatUtils;
import vn.com.nimbus.common.utils.ParseExtraDataUtils;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.BlogContent;
import vn.com.nimbus.data.domain.BlogTag;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.domain.Category;
import vn.com.nimbus.data.domain.Tag;
import vn.com.nimbus.data.domain.User;
import vn.com.nimbus.data.domain.constant.BlogStatus;
import vn.com.nimbus.data.domain.constant.PublicResponseType;
import vn.com.nimbus.data.repository.BlogCategoryRepository;
import vn.com.nimbus.data.repository.BlogRepository;
import vn.com.nimbus.data.repository.BlogTagRepository;
import vn.com.nimbus.data.repository.BlogViewRepository;
import vn.com.nimbus.data.repository.CategoryRepository;
import vn.com.nimbus.data.repository.TagRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogPublicServiceImpl implements BlogPublicService {

    @Resource
    private BlogRepository blogRepository;

    @Resource
    private CategoryRepository categoryRepository;

    @Resource
    private BlogCategoryRepository blogCategoryRepository;

    @Resource
    private BlogViewRepository blogViewRepository;

    @Resource
    private BlogTagRepository blogTagRepository;

    @Resource
    private TagRepository tagRepository;

    @Resource
    private BlogViewService viewService;

    @Override
    public FeatureResponse getFeature() {
        final Pageable pageable = PageRequest.of(0, 5);
        FeatureResponse response = new FeatureResponse();
        Page<Category> page = categoryRepository.findAll(PageRequest.of(0, 10));
        List<Category> categories = page.getContent();

        List<Blog> mostViewBlogs = this.getMostViewedBlogs(Collections.emptyList());
        List<BasePublicResponse.Blog> highLights = this.extractBlogs(mostViewBlogs);
        response.setHighlights(highLights);

        List<Blog> allBlogs = new ArrayList<>(mostViewBlogs);
        List<Integer> allBlogIds = mostViewBlogs.stream().map(Blog::getId).collect(Collectors.toList());
        List<FeatureResponse.Feature> features = new ArrayList<>();
        categories.forEach(category -> {
            List<Integer> ids = blogViewRepository.getMostViewsByCategoryId(category.getId(), pageable);
            List<Integer> fetchIds = new ArrayList<>();
            List<Integer> existIds = new ArrayList<>();

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
            feature.setCategory(this.extractCategory(category));
            features.add(feature);
        });
        response.setFeatures(features);
        return response;
    }

    @Override
    public Paging<BasePublicResponse> searchBlog(String title, LimitOffsetPageable limitOffsetPageable) {
        Page<Blog> blogsPage = blogRepository.findByStatusAndTitleContains(BlogStatus.PUBLISHED, title, PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit()));
        List<BasePublicResponse.Blog> resBlogs = blogsPage.get().map(this::extractBlog).collect(Collectors.toList());
        limitOffsetPageable.setTotal(blogsPage.getTotalElements());
        BasePublicResponse response = new BasePublicResponse();
        response.setType(PublicResponseType.SEARCH);
        response.setBlogs(resBlogs);
        response.setHighlights(this.extractBlogs(
                this.getMostViewedBlogs(
                        blogsPage.get()
                                .map(Blog::getId)
                                .collect(Collectors.toList())
                )
            )
        );
        return new Paging<>(response, limitOffsetPageable);
    }

    @Override
    public Mono<Object> getBlog(String slug, LimitOffsetPageable limitOffsetPageable) {
        log.info("Build data from slug: {}", slug);

        Blog blog = blogRepository.findBySlugAndStatus(slug, BlogStatus.PUBLISHED);
        Paging<BasePublicResponse> res;
        if (blog != null) {
            res = this.buildBaseRes(PublicResponseType.BLOG, blog, limitOffsetPageable);
            viewService.addView(blog);
            return Mono.just(res);
        }

        Category category = categoryRepository.findBySlug(slug);
        if (category != null) {
            res = this.buildBaseRes(PublicResponseType.CATEGORY, category, limitOffsetPageable);
            return Mono.just(res);
        }

        Optional<Tag> tagOpt = tagRepository.findBySlug(slug);
        if (tagOpt.isPresent()) {
            Tag tag = tagOpt.get();
            res = this.buildBaseRes(PublicResponseType.TAG, tag, limitOffsetPageable);
            return Mono.just(res);
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
                BasePublicResponse.BlogDetail blogDetail = new BasePublicResponse.BlogDetail();
                blogDetail.setId(blog.getId());
                blogDetail.setTitle(blog.getTitle());
                blogDetail.setSlug(blog.getSlug());
                blogDetail.setDescription(blog.getDescription());
                blogDetail.setThumbnail(blog.getThumbnail());
                blogDetail.setUpdatedAt(FormatUtils.formatLocalDateTime(blog.getUpdatedAt()));
                blogDetail.setReadingTime("5 phút đọc");
                blogDetail.setViewsCount(blog.getViews().size() + 1);
                blogDetail.setCommentCount(0);

                List<BlogPublicDetailResponse.Content> contents = blog.getContents().stream()
                        .sorted(Comparator.comparing(BlogContent::getPosition))
                        .map(c -> {
                            BlogPublicDetailResponse.Content content = new BlogPublicDetailResponse.Content();
                            content.setId(c.getId());
                            content.setContent(c.getContent());
                            return content;
                        }).collect(Collectors.toList());
                blogDetail.setContents(contents);
                List<BasePublicResponse.Author> authors = this.extractAuthors(blog.getAuthors());
                blogDetail.setAuthors(authors);
                List<BasePublicResponse.Category> categories = this.extractCategories(blog.getCategories().stream().map(BlogCategory::getCategory).collect(Collectors.toList()));
                blogDetail.setCategories(categories);
                List<BasePublicResponse.Tag> tags = this.extractTags(blog.getTags());
                blogDetail.setTags(tags);
                blogDetail.setExtraData(ParseExtraDataUtils.parseBlogExtraData(blog));
                response.setBlog(blogDetail);

                blogs = Collections.singletonList(blog);
                break;
            case CATEGORY:
                Category category = (Category) data;
                BasePublicResponse.Category categoryDetail = new BasePublicResponse.Category();
                categoryDetail.setId(category.getId());
                categoryDetail.setSlug(category.getSlug());
                categoryDetail.setTitle(category.getTitle());
                response.setCategory(categoryDetail);

                Page<BlogCategory> pageCat = blogCategoryRepository.findByCategoryId(
                        category.getId(),
                        BlogStatus.PUBLISHED,
                        PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit())
                );
                limitOffsetPageable.setTotal(pageCat.getTotalElements());
                blogs = pageCat.getContent().stream()
                        .map(BlogCategory::getBlog)
                        .collect(Collectors.toList());
                break;
            case TAG:
                Tag tag = (Tag) data;
                BasePublicResponse.Tag tagDetail = new BasePublicResponse.Tag();
                tagDetail.setId(tag.getId());
                tagDetail.setSlug(tag.getSlug());
                tagDetail.setTitle(tag.getTitle());
                response.setTag(tagDetail);
                Page<BlogTag> pageTag = blogTagRepository.findByTag(
                        tag.getId(),
                        BlogStatus.PUBLISHED,
                        PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit())
                );
                limitOffsetPageable.setTotal(pageTag.getTotalElements());
                blogs = pageTag.getContent().stream()
                        .map(BlogTag::getBlog)
                        .collect(Collectors.toList());
                break;
            default:
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (!type.equals(PublicResponseType.BLOG)) {
            List<BasePublicResponse.Blog> blogRes = this.extractBlogs(blogs);
            response.setBlogs(blogRes);
        }

        List<Integer> excludeIds = blogs.stream().map(Blog::getId).collect(Collectors.toList());
        List<Blog> mostViewedBlogs = this.getMostViewedBlogs(excludeIds);
        response.setHighlights(this.extractBlogs(mostViewedBlogs));

        return new Paging<>(response, limitOffsetPageable);
    }

    private List<BasePublicResponse.Blog> extractBlogs(List<Blog> blogs) {
        return blogs.stream()
                .sorted(Comparator.comparing(Blog::getUpdatedAt))
                .map(blog -> {
                    BasePublicResponse.Blog res = this.extractBlog(blog);

                    List<BasePublicResponse.Category> categories = this.extractCategories(blog.getCategories()
                            .stream()
                            .map(BlogCategory::getCategory)
                            .collect(Collectors.toList()));
                    res.setCategories(categories);

                    return res;
                })
                .collect(Collectors.toList());
    }

    private BasePublicResponse.Blog extractBlog(Blog blog) {
        BasePublicResponse.BlogDetail res = new BasePublicResponse.BlogDetail();
        res.setId(blog.getId());
        res.setTitle(blog.getTitle());
        res.setSlug(blog.getSlug());
        res.setThumbnail(blog.getThumbnail());
        res.setDescription(blog.getDescription());
        res.setReadingTime("5 phút đọc");
        res.setUpdatedAt(FormatUtils.formatLocalDateTime(blog.getUpdatedAt()));
        res.setViewsCount(blog.getViews().size());
        res.setCommentCount(0);

        List<BasePublicResponse.Author> authors = this.extractAuthors(blog.getAuthors());
        res.setAuthors(authors);

        List<BasePublicResponse.Tag> tags = extractTags(blog.getTags());
        res.setTags(tags);
        return res;
    }

    private List<BasePublicResponse.Tag> extractTags(Set<BlogTag> tags) {
        return tags.stream()
                    .map(BlogTag::getTag)
                    .map(t -> {
                        BasePublicResponse.Tag tag = new BasePublicResponse.Tag();
                        tag.setTitle(t.getTitle());
                        tag.setSlug(t.getSlug());
                        return tag;
                    })
                    .collect(Collectors.toList());
    }

    private List<BasePublicResponse.Category> extractCategories(List<Category> categories) {
        return categories.stream().map(this::extractCategory).collect(Collectors.toList());
    }

    private BasePublicResponse.Category extractCategory(Category category) {
        BasePublicResponse.Category ca = new BasePublicResponse.Category();
        ca.setId(category.getId());
        ca.setSlug(category.getSlug());
        ca.setTitle(category.getTitle());
        return ca;
    }

    private List<BasePublicResponse.Author> extractAuthors(Set<User> user) {
        return user.stream()
                .map(u -> {
                    BasePublicResponse.Author author = new BasePublicResponse.Author();
                    author.setId(u.getId());
                    author.setAvatar(u.getAvatar());
                    author.setFirstName(u.getFirstName());
                    author.setLastName(u.getLastName());
                    return author;
                })
                .collect(Collectors.toList());
    }

    private List<Blog> getMostViewedBlogs(List<Integer> excludeIds) {
        if (CollectionUtils.isEmpty(excludeIds))
            excludeIds = Collections.singletonList(-1);

        List<Integer> page = blogViewRepository.getMostViews(excludeIds, PageRequest.of(0, 5));
        return blogRepository.findByIdIn(page);
    }

}
