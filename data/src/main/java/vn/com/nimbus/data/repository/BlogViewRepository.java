package vn.com.nimbus.data.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.BlogView;

import java.util.List;

@Repository
public interface BlogViewRepository extends JpaRepository<BlogView, Integer> {
    void deleteByBlogId(Long blogId);

    long countByBlogId(Long blogId);

    List<BlogView> findByBlogId(Long blogId);

    @Query(
            value = "SELECT b.id " +
                    "FROM Blog b " +
                    "LEFT JOIN BlogView bv ON b.id = bv.blogId " +
                    "WHERE b.status = vn.com.nimbus.data.domain.constant.BlogStatus.PUBLISHED AND b.id NOT IN ?1 " +
                    "GROUP BY b.id " +
                    "ORDER BY COUNT(bv.blogId) DESC, b.createdAt DESC"
    )
    List<Long> getMostViews(List<Long> excludeIds, Pageable pageable);

    @Query(
            value = "SELECT b.id " +
                    "FROM Blog b " +
                    "LEFT JOIN BlogCategory bc ON b.id = bc.id.blogId " +
                    "LEFT JOIN BlogView bv ON b.id = bv.blogId " +
                    "WHERE bc.id.categoryId = ?1 AND b.status = vn.com.nimbus.data.domain.constant.BlogStatus.PUBLISHED " +
                    "GROUP BY b.id " +
                    "ORDER BY COUNT(bv.blogId) DESC"
    )
    List<Long> getMostViewsByCategoryId(Long categoryId, Pageable pageable);

}
