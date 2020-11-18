package vn.com.nimbus.blog.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.com.nimbus.blog.api.model.request.BlogRequest;
import vn.com.nimbus.blog.api.service.BlogContentService;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.data.domain.BlogContent;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.domain.constant.BlogContentType;
import vn.com.nimbus.data.repository.BlogContentRepository;

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
    public List<BlogContent> saveContents(Blog blog, List<BlogRequest.Content> contents) {
        log.info("Saving content for blog {} with total {} content", blog.getId(), contents.size());
        List<BlogContent> existContents = !CollectionUtils.isEmpty(blog.getContents()) ? new ArrayList<>(blog.getContents()) : new ArrayList<>();

        List<BlogContent> updateContent = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            BlogRequest.Content request = contents.get(i);
            BlogContent content = new BlogContent();
            content.setId(request.getId());
            content.setContent(request.getContent());
            content.setBlogId(blog.getId());
            content.setPosition(i);
            try {
                content.setType(BlogContentType.valueOf(request.getType()));
            } catch (IllegalArgumentException e) {
                log.warn("Illegal content type, ex: {}", e.getMessage());
                throw new BaseException(ErrorCode.INVALID_PARAMETERS);
            }
            updateContent.add(content);
        }

        updateContent = blogContentRepository.saveAll(updateContent);

        List<Integer> updateContentIds = updateContent.stream()
                .map(BlogContent::getId)
                .collect(Collectors.toList());
        List<BlogContent> removeContent = existContents.stream()
                .filter(t -> !updateContentIds.contains(t.getId()))
                .collect(Collectors.toList());

        blogContentRepository.deleteAll(removeContent);

        blog.setContents(new HashSet<>(updateContent));

        return updateContent;
    }

    @Override
    public void deleteContents(List<BlogContent> contents) {
        blogContentRepository.deleteAll(contents);
    }
}
