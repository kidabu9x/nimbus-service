package vn.com.nimbus.blog.internal.service;

import vn.com.nimbus.blog.internal.model.request.CreateBlogRequest;
import vn.com.nimbus.data.domain.BlogContents;
import vn.com.nimbus.data.domain.Blogs;

import java.util.List;

public interface BlogContentService {
    List<BlogContents> saveContents(Blogs blog, List<CreateBlogRequest.Content> contents);

    void deleteContents(List<BlogContents> contents);
}
