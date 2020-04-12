package vn.com.nimbus.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.nimbus.common.data.domain.BlogView;
import vn.com.nimbus.common.data.domain.Blogs;
import vn.com.nimbus.common.data.repository.BlogViewRepository;

import javax.annotation.Resource;

@Service
@Slf4j
public class BlogViewServiceImpl implements BlogViewService {
    @Resource
    private BlogViewRepository viewRepository;

    @Override
    @Transactional
    public void addView(Blogs blog) {
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
