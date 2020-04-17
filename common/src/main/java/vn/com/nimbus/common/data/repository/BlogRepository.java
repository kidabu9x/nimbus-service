package vn.com.nimbus.common.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.Blogs;
import vn.com.nimbus.common.data.domain.constant.BlogStatus;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blogs, Integer> {
    Integer countBySlugContains(String candidate);

    Blogs findBySlugAndStatus(String candidate, BlogStatus status);

    Page<Blogs> findByTitleContains(String title, Pageable pageable);

    @Query("SELECT b FROM Blogs b INNER JOIN BlogCategory bc ON bc.id.categoryId = ?2 WHERE b.title LIKE %?1%" )
    Page<Blogs> findByCategoryIdAndTitleContains(String title, Integer categoryId, Pageable pageable);

    List<Blogs> findByIdIn(List<Integer> ids);

}
