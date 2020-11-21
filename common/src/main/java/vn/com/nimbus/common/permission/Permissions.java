package vn.com.nimbus.common.permission;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.security.UserPrincipal;

public class Permissions {
    private Permissions() {
    }

    public static Mono<UserPrincipal> getCurrentUser() {
        System.out.println("permissions");
        return ReactiveSecurityContextHolder.getContext()
                .map(s -> s.getAuthentication().getPrincipal())
                .cast(UserPrincipal.class);
    }

    public static Mono<Authentication> getCurrentAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(Authentication.class);
    }
}
