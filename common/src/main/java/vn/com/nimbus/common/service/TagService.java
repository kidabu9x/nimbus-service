package vn.com.nimbus.common.service;

import vn.com.nimbus.data.domain.BlogTag;
import vn.com.nimbus.data.domain.Blogs;
import vn.com.nimbus.data.domain.Tags;

import java.util.List;

public interface TagService {
    List<Tags> saveTags(Blogs blog, List<String> tagsList);

    List<Tags> getTags();

    void deleteRelation(List<BlogTag> blogTags);
}
