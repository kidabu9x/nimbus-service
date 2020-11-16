package vn.com.nimbus.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.slugify.Slugify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.BlogContents;
import vn.com.nimbus.data.domain.Blogs;
import vn.com.nimbus.data.domain.Users;
import vn.com.nimbus.data.domain.constant.BlogStatus;
import vn.com.nimbus.data.repository.BlogRepository;
import vn.com.nimbus.data.repository.UserRepository;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.model.extra.BlogExtraData;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.paging.Paging;
import vn.com.nimbus.common.model.request.CreateBlogRequest;
import vn.com.nimbus.common.model.request.UpdateBlogRequest;
import vn.com.nimbus.common.model.response.BlogResponse;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogServiceImpl implements BlogService {
    @Resource
    private BlogRepository blogRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private TagService tagService;

    @Resource
    private BlogContentService blogContentService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private BlogViewService blogViewService;

    @Resource
    private BlogCategoryService blogCategoryService;

    private final Slugify slugify = new Slugify();

    @Override
    @Transactional(readOnly = true)
    public Mono<Paging<BlogResponse>> getBlogs(String title, Integer categoryId, Integer limit, Integer offset) {
        LimitOffsetPageable limitOffsetPageable = new LimitOffsetPageable();
        limitOffsetPageable.setLimit(limit);
        limitOffsetPageable.setOffset(offset);

        Page<Blogs> blogsPage;
        if (categoryId != null) {
            blogsPage = blogRepository.findByCategoryIdAndTitleContains(title, categoryId, PageRequest.of(offset, limit));
        } else {
            blogsPage = blogRepository.findByTitleContains(title, PageRequest.of(offset, limit));
        }
        List<BlogResponse> resBlogs = blogsPage.get().map(this::buildBlogResponse).collect(Collectors.toList());
        limitOffsetPageable.setTotal(blogsPage.getTotalElements());
        Paging<BlogResponse> paging = new Paging<>(resBlogs, limitOffsetPageable);
        return Mono.just(paging);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<BlogResponse> getBlog(Integer blogId) {
        Optional<Blogs> blogOpt = blogRepository.findById(blogId);
        if (!blogOpt.isPresent())
            throw new AppException(AppExceptionCode.BLOG_NOT_FOUND);
        return Mono.just(this.buildBlogResponse(blogOpt.get()));
    }

    private BlogResponse buildBlogResponse(Blogs blog) {
        log.info("Build blog detail, blog id : {}", blog.getId());
        BlogResponse response = new BlogResponse();
        response.setId(blog.getId());
        response.setTitle(blog.getTitle());
        response.setSlug(blog.getSlug());
        response.setDescription(blog.getDescription());
        response.setThumbnail(blog.getThumbnail());
        response.setUpdatedAt(this.formatLocalDateTime(blog.getUpdatedAt()));
        response.setStatus(blog.getStatus());

        List<BlogResponse.Content> contents = blog.getContents()
                .stream()
                .sorted(Comparator.comparing(BlogContents::getPosition))
                .map(b -> {
                    BlogResponse.Content content = new BlogResponse.Content();
                    content.setId(b.getId());
                    content.setContent(b.getContent());
                    content.setType(b.getType());
                    content.setPosition(b.getPosition());
                    return content;
                })
                .collect(Collectors.toList());
        response.setContents(contents);

        if (!CollectionUtils.isEmpty(blog.getCategories())) {
            List<BlogResponse.Category> categories = blog.getCategories()
                    .stream()
                    .map(BlogCategory::getCategory)
                    .map(c -> {
                        BlogResponse.Category category = new BlogResponse.Category();
                        category.setId(c.getId());
                        category.setTitle(c.getTitle());
                        return category;
                    })
                    .collect(Collectors.toList());
            response.setCategories(categories);
        } else {
            response.setCategories(new ArrayList<>());
        }

        List<String> tags = blog.getTags() != null ? blog.getTags().stream().map(t -> t.getTag().getTitle()).collect(Collectors.toList()) : new ArrayList<>();
        response.setTags(tags);

        BlogExtraData extraData;
        if (blog.getExtraData() != null) {
            JsonParseService<BlogExtraData> jsonParseService = new JsonParseService<>();
            extraData = jsonParseService.toEntityData(blog.getExtraData(), BlogExtraData.class);
            if (StringUtils.isEmpty(extraData.getFacebookPixelId())) {
                extraData.setFacebookPixelId("");
            }
            if (StringUtils.isEmpty(extraData.getGoogleAnalyticsId())) {
                extraData.setGoogleAnalyticsId("");
            }
        } else {
            extraData = new BlogExtraData();
            extraData.setFacebookPixelId("");
            extraData.setGoogleAnalyticsId("");
        }
        response.setExtraData(extraData);

        return response;
    }

    private String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null)
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    @Override
    @Transactional
    public void deleteBlog(Integer blogId) {
        Optional<Blogs> blogOpt = blogRepository.findById(blogId);
        if (!blogOpt.isPresent())
            throw new AppException(AppExceptionCode.BLOG_NOT_FOUND);

        Blogs blog = blogOpt.get();
        blogContentService.deleteContents(new ArrayList<>(blogOpt.get().getContents()));
        tagService.deleteRelation(new ArrayList<>(blog.getTags()));
        categoryService.deleteRelation(new ArrayList<>(blog.getCategories()));
        blogViewService.deleteByBlogId(blogId);
        blogCategoryService.deleteByBlogId(blogId);
        blogRepository.delete(blogOpt.get());
    }

    @Override
    @Transactional
    public Mono<BlogResponse> createBlog(CreateBlogRequest request) {
        Blogs blog = new Blogs();
        this.saveBlog(blog, request);
        return this.getBlog(blog.getId());
    }

    @Override
    @Transactional
    public void updateBlog(UpdateBlogRequest request) {
        Optional<Blogs> blogOpt = blogRepository.findById(request.getId());
        if (!blogOpt.isPresent())
            throw new AppException(AppExceptionCode.BLOG_NOT_FOUND);
        Blogs blog = blogOpt.get();
        this.saveBlog(blog, request);
    }

    private void saveBlog(Blogs blog, CreateBlogRequest request) {
        Optional<Users> userOpt = userRepository.findById(request.getUserId());
        if (!userOpt.isPresent())
            throw new AppException(AppExceptionCode.USER_NOT_FOUND);

        try {
            blog.setStatus(!StringUtils.isEmpty(request.getStatus()) ? BlogStatus.valueOf(request.getStatus()) : BlogStatus.DISABLED);
        } catch (IllegalArgumentException ex) {
            log.error("Fail to parse request create blog status, ex: {}", ex.getMessage());
            throw new AppException(AppExceptionCode.UNSUPPORTED_BLOG_STATUS);
        }
        blog.setTitle(request.getTitle());
        blog.setThumbnail(request.getThumbnail());
        blog.setDescription(request.getDescription());

        if (StringUtils.isEmpty(blog.getSlug())) {
            blog.setSlug(this.generateSlug(blog.getTitle()));
        }
        if (request.getExtraData() != null) {
            try {
                BlogExtraData extraData = new BlogExtraData();
                extraData.setFacebookPixelId(request.getExtraData().getFacebookPixelId());
                extraData.setGoogleAnalyticsId(request.getExtraData().getGoogleAnalyticsId());
                ObjectMapper mapper = new ObjectMapper();
                mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
                String extraDataStr = mapper.writeValueAsString(extraData);
                blog.setExtraData(extraDataStr);
            } catch (JsonProcessingException e) {
                log.error("Fail to parse blog extra data: {}", request.getExtraData());
                throw new AppException(AppExceptionCode.INTERNAL_SERVER_ERROR);
            }
        }
        blog = blogRepository.save(blog);
        blogContentService.saveContents(blog, request.getContents());
        tagService.saveTags(blog, request.getTags());
        categoryService.updateBlogCategories(blog, request.getCategories());
        this.saveAuthors(userOpt.get(), blog);
    }

    private void saveAuthors(Users user, Blogs blog) {
        Set<Users> users = !CollectionUtils.isEmpty(blog.getAuthors()) ? blog.getAuthors() : new HashSet<>();
        boolean isAuthor = users.stream().anyMatch(u -> u.getId().equals(user.getId()));
        if (!isAuthor)
            users.add(user);
        blog.setAuthors(users);
    }

    private String generateSlug(String title) {
        String slug = slugify.slugify(title);
        Integer count = blogRepository.countBySlugContains(slug);
        if (count > 0)
            slug = slug.concat("-").concat(Integer.toString(count));

        return slug;
    }

}
