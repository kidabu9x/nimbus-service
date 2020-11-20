package vn.com.nimbus.auth.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthConfig {
    private Integer port;
    private String host;
    private Security security;

    @Getter
    @Setter
    public static class Security {
        private String jwtMobilePublicKey;
    }
}
