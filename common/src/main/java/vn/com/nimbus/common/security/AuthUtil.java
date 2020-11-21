package vn.com.nimbus.common.security;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collections;
import java.util.Date;

public interface AuthUtil {

    String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

    String BEARER = "Bearer";

    Claims decode(String token);

    default Authentication auth(ServerWebExchange serverWebExchange, AuthenticationProvider authenticationProvider) {
        var request = serverWebExchange.getRequest();
        var authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            return null;
        }

        var authToken = authHeader.substring(BEARER.length()).strip();

        // Decode jwt
        Claims claims = decode(authToken);

        // Validate jwt
        if (claims == null || this.validateToken(claims)) {
            return null;
        }


        // Get user details from jwt
        var userDetails = authenticationProvider.getUserDetails(claims);
        if (userDetails == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(userDetails, authToken, Collections.emptyList());
    }

    private boolean validateToken(Claims claims) {
        return !claims.getExpiration().after(new Date());
    }
}
