package vn.com.nimbus.common.service;

import io.jsonwebtoken.Claims;
import vn.com.nimbus.data.domain.User;

public interface JwtService {
    String createJwt(User user);

    Claims parseToken(String token);
}
