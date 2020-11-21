package vn.com.nimbus.blog.internal.service.impl;

import com.github.slugify.Slugify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import vn.com.nimbus.blog.internal.model.mapper.BlogMapper;
import vn.com.nimbus.blog.internal.model.request.BlogRequest;
import vn.com.nimbus.blog.internal.model.response.BlogDetailResponse;
import vn.com.nimbus.blog.internal.model.response.BlogResponse;
import vn.com.nimbus.blog.internal.service.BlogService;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.data.domain.*;
import vn.com.nimbus.data.domain.constant.BlogContentType;
import vn.com.nimbus.data.domain.constant.BlogStatus;
import vn.com.nimbus.data.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final BlogContentRepository blogContentRepository;
    private final TagRepository tagRepository;
    private final BlogTagRepository blogTagRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogAuthorRepository blogAuthorRepository;
    private final BlogViewRepository blogViewRepository;
    private final CategoryRepository categoryRepository;

    private final Slugify slugify = new Slugify();

    @Autowired
    public BlogServiceImpl(
            BlogRepository blogRepository,
            UserRepository userRepository,
            BlogContentRepository blogContentRepository,
            TagRepository tagRepository,
            BlogTagRepository blogTagRepository,
            BlogCategoryRepository blogCategoryRepository,
            BlogAuthorRepository blogAuthorRepository,
            BlogViewRepository blogViewRepository,
            CategoryRepository categoryRepository
    ) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.blogContentRepository = blogContentRepository;
        this.tagRepository = tagRepository;
        this.blogTagRepository = blogTagRepository;
        this.blogCategoryRepository = blogCategoryRepository;
        this.blogAuthorRepository = blogAuthorRepository;
        this.blogViewRepository = blogViewRepository;
        this.categoryRepository = categoryRepository;
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
        return data.map(BlogMapper.INSTANCE::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogDetailResponse getBlog(Long blogId) {
        if (blogId == null) {
            throw new BaseException(ErrorCode.INVALID_PARAMETERS);
        }
        Optional<Blog> blogOpt = blogRepository.findById(blogId);
        if (blogOpt.isEmpty())
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "blog not found by id: " + blogId);
        return this.buildBlogResponse(blogOpt.get());
    }

    private BlogDetailResponse buildBlogResponse(Blog blog) {
        log.info("Build blog detail, blog id : {}", blog.getId());
        BlogMapper mapper = BlogMapper.INSTANCE;

        BlogDetailResponse response = mapper.toDetailResponse(blog);

        final Long blogId = blog.getId();
        BlogContent content = blogContentRepository.findByBlogId(blogId);
        response.setContent(content.getContent());

        List<BlogTag> blogTags = blogTagRepository.findById_BlogId(blogId);
        List<Long> tagIds = blogTags.stream()
                .map(BlogTag::getId)
                .map(BlogTagID::getTagId)
                .collect(Collectors.toList());
        List<Tag> tags = tagRepository.findAllById(tagIds);
        List<String> strTags = tags.stream().map(Tag::getTitle).collect(Collectors.toList());
        response.setTags(strTags);

        List<BlogCategory> blogCategories = blogCategoryRepository.findById_BlogId(blogId);
        List<Long> categoryIds = blogCategories.stream()
                .map(BlogCategory::getId)
                .map(BlogCategoryID::getCategoryId)
                .collect(Collectors.toList());
        response.setCategories(categoryIds);

        return response;
    }

    @Override
    @Transactional
    public boolean deleteBlog(Long blogId) {
        Optional<Blog> blogOpt = blogRepository.findById(blogId);
        if (blogOpt.isEmpty()) {
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        BlogContent content = blogContentRepository.findByBlogId(blogId);
        blogContentRepository.delete(content);

        List<BlogTag> tags = blogTagRepository.findById_BlogId(blogId);
        blogTagRepository.deleteAll(tags);

        List<BlogCategory> categories = blogCategoryRepository.findById_BlogId(blogId);
        blogCategoryRepository.deleteAll(categories);

        List<BlogView> views = blogViewRepository.findByBlogId(blogId);
        blogViewRepository.deleteAll(views);

        List<BlogAuthor> authors = blogAuthorRepository.findById_BlogId(blogId);
        blogAuthorRepository.deleteAll(authors);

        Blog blog = blogOpt.get();
        blogRepository.delete(blog);
        return true;
    }

    @Override
    @Transactional
    public BlogDetailResponse saveBlog(BlogRequest request) {
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
        blog = blogRepository.save(blog);

        final Long blogId = blog.getId();

        this.saveContent(request, blogId);
        this.saveTags(blogId, request.getTags());
        this.saveCategories(blogId, request.getCategories());
        this.saveAuthor(blogId, request.getUserId());

        return this.getBlog(blog.getId());
    }

    private void saveContent(BlogRequest request, Long blogId) {
        BlogContent content = blogContentRepository.findByBlogId(blogId);
        if (content == null) {
            content = new BlogContent();
            content.setPosition(0);
            content.setType(BlogContentType.HTML);
            content.setBlogId(blogId);
        }
        content.setContent(request.getContent());
        blogContentRepository.save(content);
    }

    private void saveTags(Long blogId, Set<String> setTags) {
        List<String> reqTags = CollectionUtils.isEmpty(setTags) ? new ArrayList<>() : setTags.stream().map(String::trim).collect(Collectors.toList());

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

        List<BlogTag> oldLinked = blogTagRepository.findById_BlogId(blogId);
        blogTagRepository.deleteAll(oldLinked);

        List<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toList());
        List<BlogTag> newLinked = tagIds.stream().map(id -> new BlogTag(new BlogTagID(id, blogId))).collect(Collectors.toList());
        blogTagRepository.saveAll(newLinked);
    }

    private void saveCategories(final Long blogId, Set<Long> setCategories) {
        List<Long> reqCatIds = CollectionUtils.isEmpty(setCategories) ? new ArrayList<>() : new ArrayList<>(setCategories);

        if (!CollectionUtils.isEmpty(reqCatIds)) {
            List<Category> existCat = categoryRepository.findAllById(reqCatIds);
            if (existCat.size() != reqCatIds.size()) {
                throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "Some categories not found");
            }
        }

        List<BlogCategory> oldLinked = blogCategoryRepository.findById_BlogId(blogId);
        blogCategoryRepository.deleteAll(oldLinked);

        List<BlogCategory> newLinked = reqCatIds.stream().map(id -> new BlogCategory(new BlogCategoryID(id, blogId))).collect(Collectors.toList());
        blogCategoryRepository.saveAll(newLinked);
    }

    private void saveAuthor(final Long blogId, Long userId) {
        List<BlogAuthor> currentAuthors = blogAuthorRepository.findById_BlogId(blogId);
        List<Long> currentAuthorIds = currentAuthors
                .stream()
                .map(BlogAuthor::getId)
                .map(BlogAuthorID::getAuthorId)
                .collect(Collectors.toList());

        if (!currentAuthorIds.contains(userId)) {
            blogAuthorRepository.save(new BlogAuthor(new BlogAuthorID(userId, blogId)));
        }
    }

    private String generateSlug(String title) {
        String slug = slugify.slugify(title);
        Integer count = blogRepository.countBySlugContains(slug);
        if (count > 0)
            slug = slug.concat("-").concat(Integer.toString(count));

        return slug;
    }
}
