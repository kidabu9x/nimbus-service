package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.Tag;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Integer countBySlugContains(String candidate);

    Tag findBySlug(String slug);

    Tag findByTitleAndSlug(String title, String slug);

    List<Tag> findByTitleIn(List<String> tags);

    @Query(
            value = "SELECT t FROM Tag t INNER JOIN BlogTag blogTag ON blogTag.id.tagId = t.id WHERE blogTag.id.blogId = ?1"
    )
    List<Tag> findLinkedTags(Long blogId);

}
