package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.BlogAuthor;
import vn.com.nimbus.data.domain.BlogAuthorID;

import java.util.List;

@Repository
public interface BlogAuthorRepository extends JpaRepository<BlogAuthor, BlogAuthorID> {
    List<BlogAuthor> findById_BlogId(Long id);
    List<BlogAuthor> findById_BlogIdIn(List<Long> ids);
}
