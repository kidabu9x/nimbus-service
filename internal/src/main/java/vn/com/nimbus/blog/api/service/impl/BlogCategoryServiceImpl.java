package vn.com.nimbus.blog.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.nimbus.blog.api.service.BlogCategoryService;
import vn.com.nimbus.data.repository.BlogCategoryRepository;

import javax.annotation.Resource;

@Service
@Slf4j
public class BlogCategoryServiceImpl implements BlogCategoryService {
    @Resource
    private BlogCategoryRepository blogCategoryRepository;
    @Override
    public void deleteByBlogId(Integer blogId) {
        blogCategoryRepository.deleteByBlogId(blogId);
    }
}
