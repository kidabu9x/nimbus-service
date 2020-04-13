package vn.com.nimbus.common.service;

import io.jsonwebtoken.Claims;
import vn.com.nimbus.common.data.domain.Users;

public interface TokenService {
    String createJwt(Users user);

    Claims parseToken(String token);
}
