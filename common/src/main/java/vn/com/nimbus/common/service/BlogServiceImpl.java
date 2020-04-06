package vn.com.nimbus.common.service;

import com.github.slugify.Slugify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.data.domain.BlogContents;
import vn.com.nimbus.common.data.domain.Blogs;
import vn.com.nimbus.common.data.domain.Categories;
import vn.com.nimbus.common.data.domain.Tags;
import vn.com.nimbus.common.data.domain.Users;
import vn.com.nimbus.common.data.domain.constant.BlogContentType;
import vn.com.nimbus.common.data.domain.constant.BlogStatus;
import vn.com.nimbus.common.data.repository.BlogContentRepository;
import vn.com.nimbus.common.data.repository.BlogRepository;
import vn.com.nimbus.common.data.repository.CategoryRepository;
import vn.com.nimbus.common.data.repository.TagRepository;
import vn.com.nimbus.common.data.repository.UserRepository;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.model.request.CreateBlogRequest;
import vn.com.nimbus.common.model.response.BlogResponse;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogServiceImpl implements BlogService {
    @Resource
    private BlogRepository blogRepository;

    @Resource
    private BlogContentRepository blogContentRepository;

    @Resource
    private TagRepository tagRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private CategoryRepository categoryRepository;

    private final Slugify slugify = new Slugify();

    @Override
    @Transactional(readOnly = true)
    public Flux<BlogResponse> getBlogs() {
        return Flux.fromStream(blogRepository.findAll().stream().map(this::buildBlogResponse));
    }

    private BlogResponse buildBlogResponse(Blogs blog) {
        log.info("Build blog detail, blog id : {}", blog.getId());
        BlogResponse response = new BlogResponse();
        response.setId(blog.getId());
        response.setTitle(blog.getTitle());
        response.setSlug(blog.getSlug());
        response.setUpdatedAt(this.formatLocalDateTime(blog.getUpdatedAt()));

        List<BlogResponse.Author> authors = blog.getAuthors().stream()
                .map(a -> {
                    BlogResponse.Author author = new BlogResponse.Author();
                    author.setId(a.getId());
                    author.setFirstName(a.getFirstName());
                    author.setLastName(a.getLastName());
                    author.setEmail(a.getEmail());
                    author.setAvatar(a.getAvatar());
                    return author;
                })
                .collect(Collectors.toList());
        response.setAuthors(authors);

        List<BlogResponse.Content> contents = blog.getContents()
                .stream()
                .map(b -> {
                    BlogResponse.Content content = new BlogResponse.Content();
                    content.setId(b.getId());
                    content.setContent(b.getContent());
                    content.setType(b.getType());
                    return content;
                })
                .collect(Collectors.toList());
        response.setContents(contents);

        List<String> tags = blog.getTags() != null ? blog.getTags().stream().map(Tags::getTitle).collect(Collectors.toList()) : new ArrayList<>();
        response.setTags(tags);

        return response;
    }

    private String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null)
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    @Override
    public void deleteBlog(Integer blogId) {
        Optional<Blogs> blogOpt = blogRepository.findById(blogId);
        if (!blogOpt.isPresent())
            throw new AppException(AppExceptionCode.BLOG_NOT_FOUND);

        blogRepository.delete(blogOpt.get());
    }

    @Override
    @Transactional
    public Mono<BlogResponse> createBlog(Integer userId, CreateBlogRequest request) {
        Blogs blog = new Blogs();
        boolean isExist = false;
        if (request.getId() != null) {
            Optional<Blogs> blogOpt = blogRepository.findById(request.getId());
            if (!blogOpt.isPresent())
                throw new AppException(AppExceptionCode.BLOG_NOT_FOUND);
            blog = blogOpt.get();
            isExist = true;
        }

        Optional<Users> userOpt = userRepository.findById(userId);
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
        if (StringUtils.isEmpty(blog.getSlug())) {
            blog.setSlug(this.generateSlug(blog.getTitle()));
        }
        blog = blogRepository.save(blog);
        this.saveContent(blog, request.getContents());
        this.saveAuthors(userOpt.get(), blog, isExist);
        this.saveTags(blog, request.getTags());
        this.saveCategories(request, blog);
        blog = blogRepository.save(blog);
        return Mono.just(this.buildBlogResponse(blog));
    }

    private void saveCategories(CreateBlogRequest request, Blogs blog) {
        if (request.getCategoryIds() == null)
            return;

        Set<Categories> categories = new HashSet<>(categoryRepository.findAllByIdIn(request.getCategoryIds()));
        blog.setCategories(categories);
    }

    private void saveAuthors(Users user, Blogs blog, boolean isExist) {
        Set<Users> users = isExist ? blog.getAuthors() : new HashSet<>();
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

    private void saveContent(Blogs blog, List<CreateBlogRequest.Content> reqContents) {
        List<BlogContents> contents = new ArrayList<>();
        for (CreateBlogRequest.Content reqContent: reqContents) {
            BlogContents content;
            boolean isExist = false;
            if (reqContent.getId() != null) {
                content = blogContentRepository.findByIdAndBlogId(reqContent.getId(), blog.getId());
                if (content == null) {
                    throw new AppException(AppExceptionCode.BLOG_CONTENT_NOT_FOUND);
                }
                isExist = true;
            } else {
                content = new BlogContents();
            }
            content.setBlogId(blog.getId());
            content.setContent(reqContent.getContent());
            try {
                if (!isExist)
                    content.setType(BlogContentType.valueOf(reqContent.getType()));
            } catch (IllegalArgumentException e) {
                log.warn("Client passing illegal content type");
                throw new AppException(AppExceptionCode.UNSUPPORTED_CONTENT_TYPE);
            }
            contents.add(content);
        }
        blog.setContents(new HashSet<>(blogContentRepository.saveAll(contents)));
    }

    private void saveTags(Blogs blog, List<String> tagStrs) {
        Set<Tags> tags = new HashSet<>();
        if (tagStrs == null)
            return;

        Map<String, Integer> tagCount = new HashMap<>();

        for (String tagStr: tagStrs) {
            tagStr = tagStr.trim();
            String slug = slugify.slugify(tagStr);
            if (tagCount.containsKey(slug))
                tagCount.put(slug, tagCount.get(slug) + 1);
            else
                tagCount.put(slug, 0);

            String tempSlug = tagCount.get(slug) > 0 ? slug.concat("-").concat(Integer.toString(tagCount.get(slug))) : slug;
            Tags tag = tagRepository.findByTitleAndSlug(tagStr, tempSlug);
            if (tag != null) {
                tag.setUpdatedAt(LocalDateTime.now());
            } else {
                tag = new Tags();
                Integer count = tagRepository.countBySlugContains(slug);
                Integer requestCount = tagCount.get(slug);
                if ((count + requestCount) > 0)
                    slug = slug.concat("-").concat(Integer.toString(count + requestCount));
                tag.setTitle(tagStr);
                tag.setSlug(slug);
            }
            tags.add(tag);
        }
        blog.setTags(new HashSet<>(tagRepository.saveAll(tags)));
    }
}
