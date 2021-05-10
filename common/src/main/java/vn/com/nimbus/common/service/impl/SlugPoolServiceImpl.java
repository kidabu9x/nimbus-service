package vn.com.nimbus.common.service.impl;


import com.github.slugify.Slugify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.com.nimbus.common.model.dto.SlugPoolDto;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;
import vn.com.nimbus.common.service.SlugPoolService;
import vn.com.nimbus.data.domain.SlugPool;
import vn.com.nimbus.data.repository.SlugPoolRepository;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SlugPoolServiceImpl implements SlugPoolService {
    private final SlugPoolRepository slugPoolRepository;
    private final Slugify slugify = new Slugify();

    private final Map<String, SlugPoolDto> cache = new HashMap<>();

    @Autowired
    public SlugPoolServiceImpl(SlugPoolRepository slugPoolRepository) {
        this.slugPoolRepository = slugPoolRepository;
    }

    @Override
    @Transactional
    public String save(SlugPoolDto dto) {
        if (dto == null
                || dto.getTargetId() == null
                || dto.getType() == null
        ) {
            log.error("slug dto is null");
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        SlugPool entity = slugPoolRepository.findByTypeAndTargetId(dto.getType(), dto.getTargetId());
        if (entity != null) {
            return entity.getSlug();
        }

        String slug = this.generateSlug(dto.getTitle());
        if (StringUtils.isEmpty(slug)) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, "Cannot generate slug");
        }
        entity = new SlugPool();
        entity.setSlug(slug);
        entity.setTargetId(dto.getTargetId());
        entity.setType(dto.getType());
        slugPoolRepository.save(entity);
        return slug;
    }

    @Override
    public SlugPoolDto get(String slug) {
        if (StringUtils.isEmpty(slug)) {
            throw new BaseException(ErrorCode.INVALID_PARAMETERS);
        }
        slug = slug.trim();
        SlugPoolDto dto = cache.get(slug);
        if (dto != null) {
            log.info("Get slug '{}' from cache, target {} with id: {}", slug, dto.getType(), dto.getTargetId());
            return dto;
        }

        Optional<SlugPool> slugOpt = slugPoolRepository.findById(slug);
        if (slugOpt.isEmpty()) {
            log.error("Not found slug '{}' in slug pool", slug);
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        SlugPool slugPool = slugOpt.get();
        dto = new SlugPoolDto();
        dto.setTargetId(slugPool.getTargetId());
        dto.setType(slugPool.getType());
        cache.put(slug, dto);

        return dto;
    }

    @Override
    @Transactional
    public void delete(SlugPoolDto dto) {
        if (dto == null
                || dto.getTargetId() == null
                || dto.getType() == null
        ) {
            log.error("slug dto is null");
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        SlugPool entity = slugPoolRepository.findByTypeAndTargetId(dto.getType(), dto.getTargetId());
        if (entity == null) {
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        slugPoolRepository.delete(entity);
        cache.remove(entity.getSlug());
    }

    private String generateSlug(String title) {
        String slug = slugify.slugify(title);
        int count = 0;
        Optional<SlugPool> existSlug = slugPoolRepository.findById(slug);
        while (existSlug.isPresent()) {
            count++;
            existSlug = slugPoolRepository.findById(slug.concat("-").concat(Integer.toString(count)));
        }
        if (count > 0) {
            slug = slug.concat("-").concat(Integer.toString(count));
        }

        return slug;
    }
}
