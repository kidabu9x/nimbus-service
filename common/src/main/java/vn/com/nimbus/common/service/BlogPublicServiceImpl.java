package vn.com.nimbus.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.data.domain.BlogContents;
import vn.com.nimbus.common.data.domain.BlogTag;
import vn.com.nimbus.common.data.domain.Blogs;
import vn.com.nimbus.common.data.domain.Tags;
import vn.com.nimbus.common.data.domain.constant.PublicResponseType;
import vn.com.nimbus.common.data.repository.BlogRepository;
import vn.com.nimbus.common.data.repository.CategoryRepository;
import vn.com.nimbus.common.data.repository.TagRepository;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.model.extra.BlogExtraData;
import vn.com.nimbus.common.model.response.BlogPublicDetailResponse;
import vn.com.nimbus.common.model.response.BlogPublicResponse;
import vn.com.nimbus.common.utils.FormatUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogPublicServiceImpl implements BlogPublicService {

    @Resource
    private BlogRepository blogRepository;

    @Resource
    private CategoryRepository categoryRepository;

    @Resource
    private TagRepository tagRepository;

    @Override
    public Flux<BlogPublicResponse> getBySlug(String slug) {
        return null;
    }

    private BlogPublicDetailResponse buildBlogRes(Blogs blog) {
        log.info("Build blog public detail, id {}, slug {}", blog.getId(), blog.getSlug());

        BlogPublicDetailResponse response = new BlogPublicDetailResponse();
        response.setId(blog.getId());
        response.setTitle(blog.getTitle());
        response.setSlug(blog.getSlug());
        response.setType(PublicResponseType.BLOG);

        response.setDescription(blog.getDescription());
        response.setThumbnail(blog.getThumbnail());
        response.setUpdatedAt(FormatUtils.formatLocalDateTime(blog.getUpdatedAt()));

        String content = blog.getContents().stream().map(BlogContents::getContent).collect(Collectors.joining());
        response.setContent(content);

        List<BlogPublicResponse.Author> authors = blog.getAuthors()
                .stream()
                .map(a -> {
                    BlogPublicResponse.Author author = new BlogPublicResponse.Author();
                    author.setId(a.getId());
                    author.setAvatar(a.getAvatar());
                    author.setFirstName(a.getFirstName());
                    author.setLastName(a.getLastName());
                    author.setEmail(a.getEmail());
                    return author;
                })
                .collect(Collectors.toList());
        response.setAuthors(authors);

        List<String> tags = blog.getTags().stream()
                .map(blogTag -> {
                    Tags tag = blogTag.getTag();
                    return tag.getTitle();
                })
                .collect(Collectors.toList());
        response.setTags(tags);

        BlogExtraData extraData;
        if (blog.getExtraData() != null) {
            JsonParseService<BlogExtraData> jsonParseService = new JsonParseService<>();
            extraData = jsonParseService.toEntityData(blog.getExtraData(), BlogExtraData.class);
            if (StringUtils.isEmpty(extraData.getFacebookPixelId())) {
                extraData.setFacebookPixelId("");
            }
        } else {
            extraData = new BlogExtraData();
            extraData.setFacebookPixelId("");
        }
        response.setExtraData(extraData);

        return response;
    }

    @Override
    public Mono<BlogPublicDetailResponse> getBlog(String slug) {
        log.info("Build data from slug: {}", slug);

        Blogs blog = blogRepository.findBySlug(slug);
        if (blog == null)
            throw new AppException(AppExceptionCode.BLOG_NOT_FOUND);

        return Mono.just(this.buildBlogRes(blog));
    }
}
