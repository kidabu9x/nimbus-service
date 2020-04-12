package vn.com.nimbus.common.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.Blogs;

@Repository
public interface BlogRepository extends JpaRepository<Blogs, Integer> {
    Integer countBySlugContains(String candidate);

    Blogs findBySlug(String candidate);

    Page<Blogs> findByTitleContains(String title, Pageable pageable);

}
