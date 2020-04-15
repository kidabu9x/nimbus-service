package vn.com.nimbus.common.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.BlogTag;
import vn.com.nimbus.common.data.domain.BlogTagID;
import vn.com.nimbus.common.data.domain.Tags;
import vn.com.nimbus.common.data.domain.constant.BlogStatus;

import java.util.List;

@Repository
public interface BlogTagRepository extends JpaRepository<BlogTag, BlogTagID> {
    @Query(
            value = "SELECT t FROM BlogTag t WHERE t.id.blogId = ?1"
    )
    List<BlogTag> findByBlogId(Integer id);

    @Query(
            value = "SELECT bt FROM BlogTag bt INNER JOIN Blogs b ON b.id = bt.id.blogId WHERE bt.id.tagId = ?1 AND b.status = ?2",
            countQuery = "SELECT count(bt.id) FROM BlogTag bt INNER JOIN Blogs b ON b.id = bt.id.blogId WHERE bt.id.tagId = ?1 AND b.status = ?2"
    )
    Page<BlogTag> findByTag(Integer tagId, BlogStatus blogStatus, Pageable pageable);
}
