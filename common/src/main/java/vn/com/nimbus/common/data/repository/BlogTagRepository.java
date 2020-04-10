package vn.com.nimbus.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.BlogTag;
import vn.com.nimbus.common.data.domain.BlogTagID;

import java.util.List;

@Repository
public interface BlogTagRepository extends JpaRepository<BlogTag, BlogTagID> {
    @Query(
            value = "SELECT t FROM BlogTag t WHERE t.id.blogId = ?1"
    )
    List<BlogTag> findByBlogId(Integer id);
}
