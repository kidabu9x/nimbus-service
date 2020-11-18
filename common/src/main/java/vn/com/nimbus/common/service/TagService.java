package vn.com.nimbus.common.service;

import vn.com.nimbus.data.domain.BlogTag;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.domain.Tag;

import java.util.List;

public interface TagService {
    List<Tag> saveTags(Blog blog, List<String> tagsList);

    List<Tag> getTags();

    void deleteRelation(List<BlogTag> blogTags);
}
