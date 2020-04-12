package vn.com.nimbus.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.BlogView;

@Repository
public interface BlogViewRepository extends JpaRepository<BlogView, Integer> {
    void deleteByBlogId(Integer blogId);

    long countByBlogId(Integer blogId);
}
