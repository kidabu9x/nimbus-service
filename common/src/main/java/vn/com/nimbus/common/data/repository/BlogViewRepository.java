package vn.com.nimbus.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.BlogView;

import java.util.List;

@Repository
public interface BlogViewRepository extends JpaRepository<BlogView, Integer> {
    void deleteByBlogId(Integer blogId);

    long countByBlogId(Integer blogId);


    @Query(
            nativeQuery = true,
            value = "SELECT blog_id FROM blog_views bv GROUP BY blog_id ORDER BY COUNT(bv.blog_id) DESC LIMIT ?1"
    )
    List<Integer> getMostViews(Integer limit);

    @Query(
            nativeQuery = true,
            value = "SELECT COUNT(bv.blog_id) as count " +
                    "FROM blog_category bc " +
                    "LEFT JOIN blog_views bv ON bc.blog_id = bv.blog_id " +
                    "WHERE bc.category_id = ?1 " +
                    "GROUP BY bv.blog_id " +
                    "ORDER BY count " +
                    "DESC LIMIT ?2"
    )
    List<Integer> getMostViewsByCategoryId(Integer id, Integer limit);
}
