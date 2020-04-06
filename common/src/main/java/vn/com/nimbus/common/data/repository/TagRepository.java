package vn.com.nimbus.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.Tags;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tags, Integer> {
    Integer countBySlugContains(String candidate);

    Optional<Tags> findBySlug(String slug);

    Tags findByTitleAndSlug(String title, String slug);
}
