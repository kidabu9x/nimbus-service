package vn.com.nimbus.common.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfigData {
    private DBConnection dbConnection;
    private SignatureInfo signatureInfo;
    private CloudinaryConfig cloudinaryConfig;

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
    public static class SignatureInfo {
        private String privateKey;
        private String publicKey;
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
}
