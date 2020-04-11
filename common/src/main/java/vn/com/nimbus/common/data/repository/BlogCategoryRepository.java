package vn.com.nimbus.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.common.data.domain.BlogCategory;
import vn.com.nimbus.common.data.domain.BlogCategoryID;
import vn.com.nimbus.common.data.domain.Categories;

import java.util.List;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, BlogCategoryID> {
    @Query(
            value = "SELECT t FROM Categories t INNER JOIN BlogCategory blogCategory ON blogCategory.id.categoryId = t.id WHERE blogCategory.id.blogId = ?1"
    )
    List<Categories> findLinkedCategories(Integer blogId);

    List<BlogCategory> findByBlogId(Integer blogId);
}
