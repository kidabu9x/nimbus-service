package vn.com.nimbus.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.com.nimbus.common.data.domain.BlogContents;
import vn.com.nimbus.common.data.domain.Blogs;
import vn.com.nimbus.common.data.domain.constant.BlogContentType;
import vn.com.nimbus.common.data.repository.BlogContentRepository;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.model.request.CreateBlogRequest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogContentServiceImpl implements BlogContentService {

    @Resource
    private BlogContentRepository blogContentRepository;

    @Override
    @Transactional
    public List<BlogContents> saveContents(Blogs blog, List<CreateBlogRequest.Content> contents) {
        log.info("Saving content for blog {} with total {} content", blog.getId(), contents.size());
        List<BlogContents> existContents = !CollectionUtils.isEmpty(blog.getContents()) ? new ArrayList<>(blog.getContents()) : new ArrayList<>();

        List<BlogContents> updateContent = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            CreateBlogRequest.Content request = contents.get(i);
            BlogContents content = new BlogContents();
            content.setId(request.getId());
            content.setContent(request.getContent());
            content.setBlogId(blog.getId());
            content.setPosition(i);
            try {
                content.setType(BlogContentType.valueOf(request.getType()));
            } catch (IllegalArgumentException e) {
                log.warn("Illegal content type, ex: {}", e.getMessage());
                throw new AppException(AppExceptionCode.UNSUPPORTED_CONTENT_TYPE);
            }
            updateContent.add(content);
        }

        updateContent = blogContentRepository.saveAll(updateContent);

        List<Integer> updateContentIds = updateContent.stream()
                .map(BlogContents::getId)
                .collect(Collectors.toList());
        List<BlogContents> removeContent = existContents.stream()
                .filter(t -> !updateContentIds.contains(t.getId()))
                .collect(Collectors.toList());

        blogContentRepository.deleteAll(removeContent);

        blog.setContents(new HashSet<>(updateContent));

        return updateContent;
    }
}
