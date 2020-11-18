package vn.com.nimbus.blog.api.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiPublicConfig {
    private Integer port;
    private String host;
    private Security security;

    @Getter
    @Setter
    public class Security {
        private String jwtMobilePublicKey;
    }
}
