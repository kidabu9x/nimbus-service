package vn.com.nimbus.auth.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.nimbus.auth.model.response.ProfileResponse;
import vn.com.nimbus.data.domain.User;

@Mapper
public abstract class AuthMapper {
    public static final AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    public abstract ProfileResponse toProfileResponse(User user);

}
