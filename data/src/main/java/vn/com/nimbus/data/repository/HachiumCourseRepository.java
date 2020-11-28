package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.HachiumCourse;

import java.util.List;

@Repository
public interface HachiumCourseRepository extends JpaRepository<HachiumCourse, Long> {
    List<HachiumCourse> findByUrlIn(List<String> urls);

    List<HachiumCourse> findByHachiumCategoryIdIn(List<Long> hachiumCategoryIds);
}
