package vn.com.nimbus.blog.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.nimbus.blog.api.service.BlogViewService;
import vn.com.nimbus.data.domain.BlogView;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.repository.BlogViewRepository;

import javax.annotation.Resource;

@Service
@Slf4j
public class BlogViewServiceImpl implements BlogViewService {
    @Resource
    private BlogViewRepository viewRepository;

    @Override
    @Transactional
    public void addView(Blog blog) {
        BlogView view = new BlogView();
        view.setBlogId(blog.getId());
        viewRepository.save(view);
    }

    @Override
    @Transactional
    public void deleteByBlogId(Integer blogId) {
        viewRepository.deleteByBlogId(blogId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByBlogId(Integer blogId) {
        return viewRepository.countByBlogId(blogId);
    }
}
