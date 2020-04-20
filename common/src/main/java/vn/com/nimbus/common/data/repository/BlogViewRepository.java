package vn.com.nimbus.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.BlogView;
import vn.com.nimbus.common.data.domain.constant.BlogStatus;

import java.util.List;

@Repository
public interface BlogViewRepository extends JpaRepository<BlogView, Integer> {
    void deleteByBlogId(Integer blogId);

    long countByBlogId(Integer blogId);


    @Query(
            nativeQuery = true,
            value = "SELECT bv.blog_id " +
                    "FROM blog_views bv " +
                    "INNER JOIN blogs b on bv.blog_id = b.id " +
                    "WHERE (b.id <> ?2) AND (b.status = 'PUBLISHED') " +
                    "GROUP BY bv.blog_id " +
                    "ORDER BY COUNT(bv.blog_id) DESC " +
                    "LIMIT ?1"
    )
    List<Integer> getMostViews(Integer limit, Integer id);

    @Query(
            nativeQuery = true,
            value = "SELECT bv.blog_id " +
                    "FROM blog_category bc " +
                    "LEFT JOIN blogs b on bc.blog_id = b.id " +
                    "LEFT JOIN blog_views bv ON b.id = bv.blog_id " +
                    "WHERE bc.category_id = ?1 AND b.status = ?2 " +
                    "GROUP BY bv.blog_id " +
                    "ORDER BY COUNT(bv.blog_id) DESC " +
                    "LIMIT ?3"
    )
    List<Integer> getMostViewsByCategoryId(Integer id, BlogStatus blogStatus, Integer limit);
}
