package vn.com.nimbus.common.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigData {
    private DBConnection dbConnection;
    private SignatureInfo signatureInfo;

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
}
