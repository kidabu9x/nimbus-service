package vn.com.nimbus.blog.internal.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.com.nimbus.blog.internal.model.response.BlogDetailResponse;
import vn.com.nimbus.blog.internal.model.response.BlogResponse;
import vn.com.nimbus.common.utils.DateToTimestampUtil;
import vn.com.nimbus.data.domain.Blog;

@Mapper(uses = {DateToTimestampUtil.class})
public abstract class BlogMapper {
    public static final BlogMapper INSTANCE = Mappers.getMapper(BlogMapper.class);

    public abstract BlogResponse toResponse(Blog blog);

    public abstract BlogDetailResponse toDetailResponse(Blog blog);
}
