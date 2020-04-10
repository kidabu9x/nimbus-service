package vn.com.nimbus.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.Categories;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Categories, Integer> {
    List<Categories> findAllByIdIn(List<Integer> ids);

    Categories findByTitle(String title);

    Long countBySlugContains(String candidate);

    List<Categories> findAllByOrderByCreatedAt();
}
