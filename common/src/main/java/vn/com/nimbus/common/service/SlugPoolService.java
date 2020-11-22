package vn.com.nimbus.common.service;

import vn.com.nimbus.common.model.dto.SlugPoolDto;

public interface SlugPoolService {
    String save(SlugPoolDto dto);

    SlugPoolDto get(String slug);

    void delete(SlugPoolDto dto);
}
