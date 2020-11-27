package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.HachiumCategoryMapping;
import vn.com.nimbus.data.domain.HachiumCategoryMappingID;

@Repository
public interface HachiumCategoryMappingRepository extends JpaRepository<HachiumCategoryMapping, HachiumCategoryMappingID> {
}
