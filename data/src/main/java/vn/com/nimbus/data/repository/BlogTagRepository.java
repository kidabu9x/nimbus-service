package vn.com.nimbus.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.BlogTag;
import vn.com.nimbus.data.domain.BlogTagID;
import vn.com.nimbus.data.domain.constant.BlogStatus;

import java.util.List;

@Repository
public interface BlogTagRepository extends JpaRepository<BlogTag, BlogTagID> {
    List<BlogTag> findById_BlogId(Long blogId);

    @Query(
            value = "SELECT bt " +
                    "FROM BlogTag bt " +
                    "INNER JOIN Blog b ON b.id = bt.id.blogId " +
                    "WHERE bt.id.tagId = ?1 AND b.status = ?2",
            countQuery = "SELECT count(bt.id) " +
                    "FROM BlogTag bt " +
                    "INNER JOIN Blog b ON b.id = bt.id.blogId " +
                    "WHERE bt.id.tagId = ?1 AND b.status = ?2"
    )
    Page<BlogTag> findByTag(Long tagId, BlogStatus blogStatus, Pageable pageable);
}
