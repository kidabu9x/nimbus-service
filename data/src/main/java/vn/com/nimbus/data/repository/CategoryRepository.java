package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByTitle(String title);

    Category findBySlug(String slug);

    Long countBySlugContains(String candidate);

    List<Category> findAllByOrderByCreatedAt();

}
