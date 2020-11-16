package vn.com.nimbus.common.service;

import io.jsonwebtoken.Claims;
import vn.com.nimbus.data.domain.Users;

public interface JwtService {
    String createJwt(Users user);

    Claims parseToken(String token);
}
