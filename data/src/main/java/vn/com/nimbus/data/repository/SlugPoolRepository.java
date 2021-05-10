package vn.com.nimbus.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.nimbus.data.domain.SlugPool;
import vn.com.nimbus.data.domain.constant.SlugPoolType;

@Repository
public interface SlugPoolRepository extends JpaRepository<SlugPool, String> {
    Integer countBySlugContains(String slug);

    SlugPool findByTypeAndTargetId(SlugPoolType type, Long targetId);
}
