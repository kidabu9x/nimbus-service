package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.HachiumCategory;

@Repository
public interface HachiumCategoryRepository extends JpaRepository<HachiumCategory, Long> {
}
