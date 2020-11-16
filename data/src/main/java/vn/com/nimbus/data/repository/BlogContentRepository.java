package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.BlogContents;

@Repository
public interface BlogContentRepository extends JpaRepository<BlogContents, Integer> {
    BlogContents findByIdAndBlogId(Integer id, Integer blogId);
}
