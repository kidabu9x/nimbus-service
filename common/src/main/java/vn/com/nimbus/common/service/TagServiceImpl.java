package vn.com.nimbus.common.service;

import com.github.slugify.Slugify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.nimbus.data.domain.BlogTag;
import vn.com.nimbus.data.domain.BlogTagID;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.domain.Tag;
import vn.com.nimbus.data.repository.BlogTagRepository;
import vn.com.nimbus.data.repository.TagRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class TagServiceImpl implements TagService {
    @Resource
    private TagRepository tagRepository;

    @Resource
    private BlogTagRepository blogTagRepository;

    private final Slugify slugify = new Slugify();

    @Override
    public List<Tag> getTags() {
        return null;
    }

    @Override
    @Transactional
    public List<Tag> saveTags(Blog blog, List<String> tagsList) {
        Integer blogId = blog.getId();

        List<String> reqTagStr = tagsList.stream().map(String::trim).collect(Collectors.toList());
        List<Tag> linkedTags = tagRepository.findLinkedTags(blogId);
        List<String> linkedTagStr = linkedTags.stream().map(Tag::getTitle).collect(Collectors.toList());

        List<String> newTagStr = reqTagStr.stream().filter(t -> !linkedTagStr.contains(t)).collect(Collectors.toList());
        List<String> oldTagStr = linkedTagStr.stream().filter(t -> !reqTagStr.contains(t)).collect(Collectors.toList());

        List<Tag> savedTags = this.saveTags(newTagStr);
        List<BlogTag> addIds = savedTags.stream().map(t -> {
            BlogTagID id = new BlogTagID();
            id.setTagId(t.getId());
            id.setBlogId(blogId);

            BlogTag blogTag = new BlogTag();
            blogTag.setId(id);
            return blogTag;
        }).collect(Collectors.toList());
        blogTagRepository.saveAll(addIds);

        List<BlogTag> removeIds = linkedTags.stream().filter(t -> oldTagStr.contains(t.getTitle())).map(t -> {
            BlogTagID id = new BlogTagID();
            id.setTagId(t.getId());
            id.setBlogId(blogId);

            BlogTag blogTag = new BlogTag();
            blogTag.setId(id);
            return blogTag;
        }).collect(Collectors.toList());
        blogTagRepository.deleteAll(removeIds);

        List<Tag> keepTags = linkedTags.stream().filter(t -> !newTagStr.contains(t.getTitle()) && oldTagStr.contains(t.getTitle())).collect(Collectors.toList());

        return Stream.concat(savedTags.stream(), keepTags.stream()).collect(Collectors.toList());
    }

    private List<Tag> saveTags(List<String> tagStrs) {
        List<Tag> tags = new ArrayList<>();
        Map<String, Integer> tagCount = new HashMap<>();
        for (String tagStr: tagStrs) {
            String slug = slugify.slugify(tagStr);
            if (tagCount.containsKey(slug))
                tagCount.put(slug, tagCount.get(slug) + 1);
            else
                tagCount.put(slug, 0);

            Tag tag = tagRepository.findByTitleAndSlug(tagStr, slug);
            if (tag != null) {
                tags.add(tag);
                continue;
            }
            tag = new Tag();
            Integer count = tagRepository.countBySlugContains(slug);
            Integer requestCount = tagCount.get(slug);
            if ((count + requestCount) > 0)
                slug = slug.concat("-").concat(Integer.toString(count + requestCount));
            tag.setTitle(tagStr);
            tag.setSlug(slug);
            tags.add(tag);
        }
        return tagRepository.saveAll(tags);
    }

    @Override
    public void deleteRelation(List<BlogTag> blogTags) {
        blogTagRepository.deleteAll(blogTags);
    }
}
