package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.Category;
import vn.com.nimbus.data.dto.output.CategoryDto;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByTitle(String title);

    Long countBySlugContains(String candidate);

    List<Category> findAllByOrderByCreatedAt();

    @Query("SELECT new vn.com.nimbus.data.dto.output.CategoryDto(b, sp.slug) " +
            "FROM Category b " +
            "INNER JOIN SlugPool sp ON sp.targetId = b.id AND sp.type = vn.com.nimbus.data.domain.constant.SlugPoolType.CATEGORY " +
            "ORDER BY b.createdAt ASC")
    List<CategoryDto> findAllDto();
}
