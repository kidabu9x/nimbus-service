package vn.com.nimbus.blog.internal.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiInternalConfig {
    private Integer port;
    private String host;
    private Security security;

    @Getter
    @Setter
    public class Security {
        private String jwtMobilePublicKey;
    }
}
