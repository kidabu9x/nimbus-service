package vn.com.nimbus.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.domain.constant.BlogStatus;
import vn.com.nimbus.data.dto.output.BlogDto;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Integer countBySlugContains(String candidate);

    Page<Blog> findByTitleContains(String title, Pageable pageable);

    @Query("SELECT new vn.com.nimbus.data.dto.output.BlogDto(b, sp.slug) " +
            "FROM Blog b " +
            "INNER JOIN SlugPool sp ON sp.targetId = b.id AND sp.type = vn.com.nimbus.data.domain.constant.SlugPoolType.BLOG " +
            "WHERE b.status = ?1 AND b.title LIKE %?2%" )
    Page<BlogDto> findByStatusAndTitleContains(BlogStatus status, String title, Pageable pageable);

    @Query("SELECT b " +
            "FROM Blog b " +
            "INNER JOIN BlogCategory bc ON bc.id.blogId = b.id " +
            "WHERE b.title LIKE %?1% AND bc.id.categoryId = ?2" )
    Page<Blog> findByCategoryIdAndTitleContains(String title, Long categoryId, Pageable pageable);

    @Query("SELECT new vn.com.nimbus.data.dto.output.BlogDto(b, sp.slug) " +
            "FROM Blog b " +
            "INNER JOIN SlugPool sp ON sp.targetId = b.id AND sp.type = vn.com.nimbus.data.domain.constant.SlugPoolType.BLOG " +
            "WHERE b.id IN (?1)" )
    List<BlogDto> findDtoByIds(List<Long> ids);
}
