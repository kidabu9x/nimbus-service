package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.Tag;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Integer countBySlugContains(String candidate);
    Tag findBySlug(String slug);
    List<Tag> findByTitleIn(List<String> tags);
}
