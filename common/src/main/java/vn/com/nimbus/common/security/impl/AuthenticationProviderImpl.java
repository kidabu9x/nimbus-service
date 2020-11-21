package vn.com.nimbus.common.security.impl;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import vn.com.nimbus.common.model.constant.KeyConstant;
import vn.com.nimbus.common.security.AuthenticationProvider;
import vn.com.nimbus.common.security.UserPrincipal;

@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {
    @Override
    public UserPrincipal getUserDetails(Claims claims) {

        return new UserPrincipal()
                .setId(claims.get(KeyConstant.USER_ID, Long.class))
                .setEmail(claims.get(KeyConstant.EMAIL, String.class));
    }
}
