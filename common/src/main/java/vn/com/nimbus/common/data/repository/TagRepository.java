package vn.com.nimbus.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.Tags;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tags, Integer> {
    Integer countBySlugContains(String candidate);

    Optional<Tags> findBySlug(String slug);

    Tags findByTitleAndSlug(String title, String slug);

    List<Tags> findByTitleIn(List<String> title);

    @Query(
            value = "SELECT t FROM Tags t INNER JOIN BlogTag blogTag ON blogTag.id.tagId = t.id WHERE blogTag.id.blogId = ?1"
    )
    List<Tags> findLinkedTags(Integer blogId);
}
