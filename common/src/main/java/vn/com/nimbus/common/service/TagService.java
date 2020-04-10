package vn.com.nimbus.common.service;

import vn.com.nimbus.common.data.domain.Blogs;
import vn.com.nimbus.common.data.domain.Tags;

import java.util.List;

public interface TagService {
    List<Tags> saveTags(Blogs blog, List<String> tagsList);

    List<Tags> getTags();
}