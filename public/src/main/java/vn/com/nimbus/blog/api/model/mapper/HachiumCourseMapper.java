package vn.com.nimbus.blog.api.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.nimbus.blog.api.model.response.HachiumCourseResponse;
import vn.com.nimbus.data.domain.HachiumCourse;

@Mapper
public abstract class HachiumCourseMapper {

    public static final HachiumCourseMapper INSTANCE = Mappers.getMapper(HachiumCourseMapper.class);

    public abstract HachiumCourseResponse toResponse(HachiumCourse hachiumCourse);
}
