package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.HachiumCourse;

@Repository
public interface HachiumCourseRepository extends JpaRepository<HachiumCourse, Long> {
}
