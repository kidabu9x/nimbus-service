package vn.com.nimbus.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.BlogCategory;
import vn.com.nimbus.data.domain.BlogCategoryID;
import vn.com.nimbus.data.domain.constant.BlogStatus;

import java.util.List;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, BlogCategoryID> {

    List<BlogCategory> findById_BlogId(Long id);

    List<BlogCategory> findById_CategoryId(Long id);

    @Query(
            value = "SELECT bc " +
                    "FROM BlogCategory bc " +
                    "INNER JOIN Blog b ON b.id = bc.id.blogId " +
                    "WHERE bc.id.categoryId = ?1 AND b.status = ?2",
            countQuery = "SELECT count(bc.id) " +
                    "FROM BlogCategory bc " +
                    "INNER JOIN Blog b ON b.id = bc.id.blogId " +
                    "WHERE bc.id.categoryId = ?1 AND b.status = ?2"
    )
    Page<BlogCategory> findByCategoryId(Long categoryId, BlogStatus blogStatus, Pageable pageable);

}
