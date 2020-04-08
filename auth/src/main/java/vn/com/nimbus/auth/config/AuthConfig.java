package vn.com.nimbus.auth.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthConfig {
    private Integer port;
    private String host;
    private Security security;

    @Getter
    @Setter
    public class Security {
        private String jwtMobilePublicKey;
    }
}
