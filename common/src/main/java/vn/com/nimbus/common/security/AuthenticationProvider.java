package vn.com.nimbus.common.security;

import io.jsonwebtoken.Claims;

public interface AuthenticationProvider {
    UserPrincipal getUserDetails(Claims claims);
}
