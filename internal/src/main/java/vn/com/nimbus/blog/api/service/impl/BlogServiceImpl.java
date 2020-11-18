package vn.com.nimbus.blog.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.slugify.Slugify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import vn.com.nimbus.blog.api.model.request.BlogRequest;
import vn.com.nimbus.blog.api.model.response.BlogResponse;
import vn.com.nimbus.blog.api.service.BlogCategoryService;
import vn.com.nimbus.blog.api.service.BlogContentService;
import vn.com.nimbus.blog.api.service.BlogService;
import vn.com.nimbus.blog.api.service.CategoryService;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.common.model.extra.BlogExtraData;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.service.JsonParseService;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.BlogContent;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.domain.BlogTag;
import vn.com.nimbus.data.domain.BlogTagID;
import vn.com.nimbus.data.domain.Tag;
import vn.com.nimbus.data.domain.User;
import vn.com.nimbus.data.domain.constant.BlogContentType;
import vn.com.nimbus.data.domain.constant.BlogStatus;
import vn.com.nimbus.data.repository.BlogContentRepository;
import vn.com.nimbus.data.repository.BlogRepository;
import vn.com.nimbus.data.repository.BlogTagRepository;
import vn.com.nimbus.data.repository.TagRepository;
import vn.com.nimbus.data.repository.UserRepository;

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
import java.util.stream.Stream;

@Service
@Slf4j
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final BlogContentRepository blogContentRepository;
    private final TagRepository tagRepository;
    private final BlogTagRepository blogTagRepository;

    @Resource
    private BlogContentService blogContentService;

    @Resource
    private CategoryService categoryService;

//    @Resource
//    private BlogViewService blogViewService;

    @Resource
    private BlogCategoryService blogCategoryService;

    private final Slugify slugify = new Slugify();

    @Autowired
    public BlogServiceImpl(
            BlogRepository blogRepository,
            UserRepository userRepository,
            BlogContentRepository blogContentRepository,
            TagRepository tagRepository,
            BlogTagRepository blogTagRepository
    ) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.blogContentRepository = blogContentRepository;
        this.tagRepository = tagRepository;
        this.blogTagRepository = blogTagRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogResponse> getBlogs(String title, Long categoryId, Integer limit, Integer offset) {
        LimitOffsetPageable limitOffsetPageable = new LimitOffsetPageable();
        limitOffsetPageable.setLimit(limit);
        limitOffsetPageable.setOffset(offset);

        Page<Blog> data;
        if (categoryId != null) {
            data = blogRepository.findByCategoryIdAndTitleContains(title, categoryId, PageRequest.of(offset, limit));
        } else {
            data = blogRepository.findByTitleContains(title, PageRequest.of(offset, limit));
        }
        return data.map(this::buildBlogResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogResponse getBlog(Long blogId) {
        if (blogId == null) {
            throw new BaseException(ErrorCode.INVALID_PARAMETERS);
        }
        Optional<Blog> blogOpt = blogRepository.findById(blogId);
        if (blogOpt.isEmpty())
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "blog not found by id: " + blogId);
        return this.buildBlogResponse(blogOpt.get());
    }

    private BlogResponse buildBlogResponse(Blog blog) {
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
                .sorted(Comparator.comparing(BlogContent::getPosition))
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
    public boolean deleteBlog(Integer blogId) {
        Optional<Blog> blogOpt = blogRepository.findById(blogId);
        if (blogOpt.isEmpty()) {
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        Blog blog = blogOpt.get();
        blogContentService.deleteContents(new ArrayList<>(blogOpt.get().getContents()));
        tagService.deleteRelation(new ArrayList<>(blog.getTags()));
        categoryService.deleteRelation(new ArrayList<>(blog.getCategories()));
        blogViewService.deleteByBlogId(blogId);
        blogCategoryService.deleteByBlogId(blogId);
        blogRepository.delete(blogOpt.get());
        return true;
    }

    @Override
    @Transactional
    public BlogResponse saveBlog(BlogRequest request) {
        if (request == null) {
            throw new BaseException(ErrorCode.INVALID_PARAMETERS);
        }
        Blog blog = new Blog();
        if (request.getId() != null) {
            Optional<Blog> blogOpt = blogRepository.findById(request.getId());
            if (blogOpt.isEmpty()) {
                throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            blog = blogOpt.get();
        }

        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        if (blog.getStatus() == null) {
            blog.setStatus(request.getStatus() != null ? request.getStatus() : BlogStatus.DISABLED);
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
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        blog = blogRepository.save(blog);

        final Long blogId = blog.getId();

        BlogContent content = blogContentRepository.findByBlogId(blogId);
        if (content == null) {
            content = new BlogContent();
            content.setPosition(0);
            content.setType(BlogContentType.HTML);
            content.setBlogId(blogId);
        }
        content.setContent(request.getContent());
        blogContentRepository.save(content);

        this.saveTags(blogId, request.getTags());
        categoryService.updateBlogCategories(blog, request.getCategories());


        Set<User> users = !CollectionUtils.isEmpty(blog.getAuthors()) ? blog.getAuthors() : new HashSet<>();
        boolean isAuthor = users.stream().anyMatch(u -> u.getId().equals(user.getId()));
        if (!isAuthor)
            users.add(user);
        blog.setAuthors(users);

        return this.getBlog(blog.getId());
    }

    private void saveTags(Long blogId, Set<String> setTags) {
        List<String> reqTags = setTags.stream().map(String::trim).collect(Collectors.toList());

        List<Tag> tags = tagRepository.findByTitleIn(reqTags);

        if (tags.size() != reqTags.size()) {
            List<String> existTags = tags.stream().map(Tag::getTitle).collect(Collectors.toList());
            List<String> newTags = reqTags.stream().filter(t -> !existTags.contains(t)).collect(Collectors.toList());
            for (String newTag : newTags) {
                String slug = slugify.slugify(newTag);
                Tag tag = new Tag();
                Integer count = tagRepository.countBySlugContains(slug);
                if (count > 0) {
                    slug = slug.concat("-").concat(Integer.toString(count));
                }
                tag.setTitle(newTag);
                tag.setSlug(slug);
                tags.add(tag);
            }
            tags = tagRepository.saveAll(tags);
        }

        List<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toList());


        List<Tag> linkedTags = tagRepository.findLinkedTags(blogId);
        List<String> linkedTagStr = linkedTags.stream().map(Tag::getTitle).collect(Collectors.toList());

        List<String> newTagStr = reqTagStr.stream().filter(t -> !linkedTagStr.contains(t)).collect(Collectors.toList());
        List<String> oldTagStr = linkedTagStr.stream().filter(t -> !reqTagStr.contains(t)).collect(Collectors.toList());

        List<Tag> savedTags = this.saveTags(newTagStr);
        List<BlogTag> addIds = savedTags.stream().map(t -> {
            BlogTagID id = new BlogTagID();
            id.setTagId(t.getId());
            id.setBlogId(blogId);

            BlogTag blogTag = new BlogTag();
            blogTag.setId(id);
            return blogTag;
        }).collect(Collectors.toList());
        blogTagRepository.saveAll(addIds);

        List<BlogTag> removeIds = linkedTags.stream().filter(t -> oldTagStr.contains(t.getTitle())).map(t -> {
            BlogTagID id = new BlogTagID();
            id.setTagId(t.getId());
            id.setBlogId(blogId);

            BlogTag blogTag = new BlogTag();
            blogTag.setId(id);
            return blogTag;
        }).collect(Collectors.toList());
        blogTagRepository.deleteAll(removeIds);

        List<Tag> keepTags = linkedTags.stream().filter(t -> !newTagStr.contains(t.getTitle()) && oldTagStr.contains(t.getTitle())).collect(Collectors.toList());

        return Stream.concat(savedTags.stream(), keepTags.stream()).collect(Collectors.toList());
    }

    private String generateSlug(String title) {
        String slug = slugify.slugify(title);
        Integer count = blogRepository.countBySlugContains(slug);
        if (count > 0)
            slug = slug.concat("-").concat(Integer.toString(count));

        return slug;
    }
}
