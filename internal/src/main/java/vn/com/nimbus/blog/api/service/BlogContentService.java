package vn.com.nimbus.blog.api.service;

import vn.com.nimbus.blog.api.model.request.BlogRequest;
import vn.com.nimbus.data.domain.BlogContent;
import vn.com.nimbus.data.domain.Blog;

import java.util.List;

public interface BlogContentService {
    List<BlogContent> saveContents(Blog blog, List<BlogRequest.Content> contents);

    void deleteContents(List<BlogContent> contents);
}
