package vn.com.nimbus.blog.api.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.nimbus.blog.api.model.response.CategoryResponse;
import vn.com.nimbus.common.utils.DateToTimestampUtil;
import vn.com.nimbus.data.domain.Category;

@Mapper(uses = {DateToTimestampUtil.class})
public abstract class CategoryMapper {
    public static final CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    public abstract CategoryResponse toResponse(Category category);
}