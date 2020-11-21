package vn.com.nimbus.blog.api.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.nimbus.blog.api.model.response.TagResponse;
import vn.com.nimbus.common.utils.DateToTimestampUtil;
import vn.com.nimbus.data.domain.Tag;

@Mapper(uses = {DateToTimestampUtil.class})
public abstract class TagMapper {
    public static final TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    public abstract TagResponse toResponse(Tag tag);
}