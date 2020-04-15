package vn.com.nimbus.common.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.BlogCategory;
import vn.com.nimbus.common.data.domain.BlogCategoryID;
import vn.com.nimbus.common.data.domain.Blogs;
import vn.com.nimbus.common.data.domain.Categories;
import vn.com.nimbus.common.data.domain.constant.BlogStatus;

import java.util.List;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, BlogCategoryID> {
    @Query(
            value = "SELECT t FROM Categories t INNER JOIN BlogCategory blogCategory ON blogCategory.id.categoryId = t.id WHERE blogCategory.id.blogId = ?1"
    )
    List<Categories> findLinkedCategories(Integer blogId);

    List<BlogCategory> findByBlogId(Integer blogId);

    @Query(
            value = "SELECT bc FROM BlogCategory bc INNER JOIN Blogs b ON b.id = bc.id.blogId WHERE bc.id.categoryId = ?1 AND b.status = ?2",
            countQuery = "SELECT count(bc.id) FROM BlogCategory bc INNER JOIN Blogs b ON b.id = bc.id.blogId WHERE bc.id.categoryId = ?1 AND b.status = ?2"
    )
    Page<BlogCategory> findByCategoryId(Integer categoryId, BlogStatus blogStatus, Pageable pageable);

    void deleteByBlogId(Integer blogId);

}
