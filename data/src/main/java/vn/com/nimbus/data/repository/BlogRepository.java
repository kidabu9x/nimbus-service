package vn.com.nimbus.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.domain.constant.BlogStatus;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Integer countBySlugContains(String candidate);

    Blog findBySlugAndStatus(String candidate, BlogStatus status);

    Page<Blog> findByTitleContains(String title, Pageable pageable);

    Page<Blog> findByStatusAndTitleContains(BlogStatus status, String title, Pageable pageable);

    @Query("SELECT b " +
            "FROM Blog b " +
            "INNER JOIN BlogCategory bc ON bc.id.blogId = b.id " +
            "WHERE b.title LIKE %?1% AND bc.id.categoryId = ?2" )
    Page<Blog> findByCategoryIdAndTitleContains(String title, Long categoryId, Pageable pageable);

    List<Blog> findByIdIn(List<Long> ids);

}
