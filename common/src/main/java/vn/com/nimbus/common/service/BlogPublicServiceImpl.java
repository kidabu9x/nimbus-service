package vn.com.nimbus.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.data.domain.BlogCategory;
import vn.com.nimbus.common.data.domain.BlogContents;
import vn.com.nimbus.common.data.domain.BlogTag;
import vn.com.nimbus.common.data.domain.Blogs;
import vn.com.nimbus.common.data.domain.Categories;
import vn.com.nimbus.common.data.domain.Tags;
import vn.com.nimbus.common.data.domain.Users;
import vn.com.nimbus.common.data.domain.constant.BlogStatus;
import vn.com.nimbus.common.data.domain.constant.PublicResponseType;
import vn.com.nimbus.common.data.repository.BlogCategoryRepository;
import vn.com.nimbus.common.data.repository.BlogRepository;
import vn.com.nimbus.common.data.repository.BlogTagRepository;
import vn.com.nimbus.common.data.repository.BlogViewRepository;
import vn.com.nimbus.common.data.repository.CategoryRepository;
import vn.com.nimbus.common.data.repository.TagRepository;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.model.extra.BlogExtraData;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.paging.Paging;
import vn.com.nimbus.common.model.response.BasePublicResponse;
import vn.com.nimbus.common.model.response.BlogPublicDetailResponse;
import vn.com.nimbus.common.model.response.FeatureResponse;
import vn.com.nimbus.common.utils.FormatUtils;
import vn.com.nimbus.common.utils.ParseExtraDataUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    public Mono<FeatureResponse> getFeature() {
        final int LIMIT = 5;
        FeatureResponse response = new FeatureResponse();
        Page<Categories> page = categoryRepository.findAll(PageRequest.of(0, 10));
        List<Categories> categories = page.getContent();

        List<Integer> mostViewBlogIds = blogViewRepository.getMostViews(LIMIT);
        List<Blogs> mostViewBlogs = blogRepository.findByIdIn(mostViewBlogIds);
        List<BasePublicResponse.Blog> highLights = this.extractBlogs(mostViewBlogs);
        response.setHighlights(highLights);

        List<Blogs> allBlogs = new ArrayList<>(mostViewBlogs);
        List<Integer> allBlogIds = mostViewBlogIds;
        List<FeatureResponse.Feature> features = new ArrayList<>();
        categories.forEach(category -> {
            List<Integer> ids = blogViewRepository.getMostViewsByCategoryId(category.getId(), LIMIT);
            List<Integer> fetchIds = new ArrayList<>();
            List<Integer> existIds = new ArrayList<>();

            ids.forEach(i -> {
                if (allBlogIds.contains(i)) {
                    existIds.add(i);
                } else {
                    fetchIds.add(i);
                }
            });

            List<Blogs> blogs = blogRepository.findByIdIn(fetchIds);
            List<Blogs> existBlogs = allBlogs.stream().filter(b -> existIds.contains(b.getId())).collect(Collectors.toList());

            allBlogIds.addAll(fetchIds);
            allBlogs.addAll(blogs);

            blogs.addAll(existBlogs);

            FeatureResponse.Feature feature = new FeatureResponse.Feature();
            feature.setBlogs(this.extractBlogs(blogs));
            feature.setCategory(this.extractCategory(category));
            features.add(feature);
        });
        response.setFeatures(features);
        return Mono.just(response);
    }

    @Override
    public Mono<Paging<BasePublicResponse>> searchBlog(String title, LimitOffsetPageable limitOffsetPageable) {
        Page<Blogs> blogsPage = blogRepository.findByStatusAndTitleContains(BlogStatus.PUBLISHED, title, PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit()));
        List<BasePublicResponse.Blog> resBlogs = blogsPage.get().map(this::extractBlog).collect(Collectors.toList());
        limitOffsetPageable.setTotal(blogsPage.getTotalElements());
        BasePublicResponse response = new BasePublicResponse();
        response.setType(PublicResponseType.SEARCH);
        response.setBlogs(resBlogs);
        response.setHighlights(this.extractBlogs(this.getMostViewedBlogs(5)));
        return Mono.just(new Paging<>(response, limitOffsetPageable));
    }

    @Override
    public Mono<Object> getBlog(String slug, LimitOffsetPageable limitOffsetPageable) {
        log.info("Build data from slug: {}", slug);
        Blogs blog = blogRepository.findBySlugAndStatus(slug, BlogStatus.PUBLISHED);
        if (blog != null) {
            BlogPublicDetailResponse res = this.buildBlogRes(blog);
            viewService.addView(blog);
            return Mono.just(res);
        }

        Categories category = categoryRepository.findBySlug(slug);
        if (category != null) {

            Paging<BasePublicResponse> res = this.buildCategoryRes(category, limitOffsetPageable);
            return Mono.just(res);
        }

        Optional<Tags> tagOpt = tagRepository.findBySlug(slug);
        if (tagOpt.isPresent()) {
            Tags tag = tagOpt.get();
            Paging<BasePublicResponse> res = this.buildTag(tag, limitOffsetPageable);
            return Mono.just(res);
        }

        throw new AppException(AppExceptionCode.SLUG_NOT_FOUND);
    }

    private BlogPublicDetailResponse buildBlogRes(Blogs blog) {
        log.info("Build blog public detail, id {}, slug {}", blog.getId(), blog.getSlug());

        BlogPublicDetailResponse response = new BlogPublicDetailResponse();
        response.setType(PublicResponseType.BLOG);
        List<Blogs> mostViewedBlogs = this.getMostViewedBlogs(5);
        response.setHighlights(this.extractBlogs(mostViewedBlogs));

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
                .sorted(Comparator.comparing(BlogContents::getPosition))
                .map(c -> {
                    BlogPublicDetailResponse.Content content = new BlogPublicDetailResponse.Content();
                    content.setId(c.getId());
                    content.setContent(c.getContent());
                    return content;
                }).collect(Collectors.toList());
        blogDetail.setContents(contents);

        List<BasePublicResponse.Author> authors = this.extractAuthors(blog.getAuthors());
        blogDetail.setAuthors(authors);

        List<BasePublicResponse.Tag> tags = this.extractTags(blog.getTags());
        blogDetail.setTags(tags);

        blogDetail.setExtraData(ParseExtraDataUtils.parseBlogExtraData(blog));

        response.setBlog(blogDetail);
        return response;
    }

    private Paging<BasePublicResponse> buildCategoryRes(Categories category, LimitOffsetPageable limitOffsetPageable) {
        log.info("Build category detail, category id: {}", category.getId());
        BasePublicResponse response = new BasePublicResponse();
        response.setType(PublicResponseType.CATEGORY);
        List<Blogs> mostViewedBlogs = this.getMostViewedBlogs(5);
        response.setHighlights(this.extractBlogs(mostViewedBlogs));
        BasePublicResponse.Category categoryDetail = new BasePublicResponse.Category();
        categoryDetail.setId(category.getId());
        categoryDetail.setSlug(category.getSlug());
        categoryDetail.setTitle(category.getTitle());
        response.setCategory(categoryDetail);

        Page<BlogCategory> page = blogCategoryRepository.findByCategoryId(category.getId(), BlogStatus.PUBLISHED, PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit()));
        List<BlogCategory> blogCategories = page.getContent();
        List<BasePublicResponse.Blog> blogs = this.extractBlogs(blogCategories.stream()
                .map(BlogCategory::getBlog)
                .collect(Collectors.toList()));
        response.setBlogs(blogs);

        limitOffsetPageable.setTotal(page.getTotalElements());

        return new Paging<>(response, limitOffsetPageable);
    }

    private Paging<BasePublicResponse> buildTag(Tags tag, LimitOffsetPageable limitOffsetPageable) {
        log.info("Build tag detail, category id: {}", tag.getId());
        BasePublicResponse response = new BasePublicResponse();
        response.setType(PublicResponseType.TAG);
        BasePublicResponse.Tag tagDetail = new BasePublicResponse.Tag();
        tagDetail.setId(tag.getId());
        tagDetail.setSlug(tag.getSlug());
        tagDetail.setTitle(tag.getTitle());
        response.setTag(tagDetail);

        List<Blogs> mostViewedBlogs = this.getMostViewedBlogs(5);
        response.setHighlights(this.extractBlogs(mostViewedBlogs));

        Page<BlogTag> page = blogTagRepository.findByTag(tag.getId(), BlogStatus.PUBLISHED, PageRequest.of(limitOffsetPageable.getOffset(), limitOffsetPageable.getLimit()));
        List<BlogTag> blogTags = page.getContent();
        List<BasePublicResponse.Blog> blogs = this.extractBlogs(blogTags.stream()
                .map(BlogTag::getBlog)
                .filter(b -> b.getStatus().equals(BlogStatus.PUBLISHED))
                .collect(Collectors.toList()));
        response.setBlogs(blogs);

        limitOffsetPageable.setTotal(page.getTotalElements());

        return new Paging<>(response, limitOffsetPageable);
    }

    private List<BasePublicResponse.Blog> extractBlogs(List<Blogs> blogs) {
        return blogs.stream()
                .sorted(Comparator.comparing(Blogs::getUpdatedAt))
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

    private BasePublicResponse.Blog extractBlog(Blogs blog) {
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

    private List<BasePublicResponse.Category> extractCategories(List<Categories> categories) {
        return categories.stream().map(this::extractCategory).collect(Collectors.toList());
    }

    private BasePublicResponse.Category extractCategory(Categories category) {
        BasePublicResponse.Category ca = new BasePublicResponse.Category();
        ca.setId(category.getId());
        ca.setSlug(category.getSlug());
        ca.setTitle(category.getTitle());
        return ca;
    }

    private List<BasePublicResponse.Author> extractAuthors(Set<Users> user) {
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

    private List<Blogs> getMostViewedBlogs(Integer limit) {
        List<Integer> mostViewBlogIds = blogViewRepository.getMostViews(limit);
        return blogRepository.findByIdIn(mostViewBlogIds);
    }

}
