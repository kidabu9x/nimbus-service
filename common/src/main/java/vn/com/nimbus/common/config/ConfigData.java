package vn.com.nimbus.common.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfigData {
    private DBConnection dbConnection;
    private OauthKey oauthKey;
    private CloudinaryConfig cloudinaryConfig;
    private GoogleOauthConfig googleOauthConfig;

    @Getter
    @Setter
    public static class DBConnection {
        private Boolean isUseHa;
        private DBInfo master;
        private DBInfo slave;

        @Getter
        @Setter
        public static class DBInfo {
            private String driverClassName;
            private String url;
            private String username;
            private String password;
            private Integer maximumPoolSize;
            private Integer minimumIdle;
        }
    }

    @Getter
    @Setter
    public static class OauthKey {
        private String privateKey;
        private String publicKey;
        private Long ttl;
    }

    @Setter
    @Getter
    public static class CloudinaryConfig {
        private String cloudName;
        private String apiKey;
        private String apiSecret;
        private String folder;
        private List<String> supportedFileFormats;
    }

    @Setter
    @Getter
    public static class GoogleOauthConfig {
        private String clientId;
        private String clientSecret;
        private String projectId;
    }
}
