package vn.com.nimbus.common.service;

import vn.com.nimbus.data.domain.BlogContents;
import vn.com.nimbus.data.domain.Blogs;
import vn.com.nimbus.common.model.request.CreateBlogRequest;

import java.util.List;

public interface BlogContentService {
    List<BlogContents> saveContents(Blogs blog, List<CreateBlogRequest.Content> contents);

    void deleteContents(List<BlogContents> contents);
}
