package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.BlogContent;

@Repository
public interface BlogContentRepository extends JpaRepository<BlogContent, Integer> {
    BlogContent findByBlogId(Long blogId);
}
