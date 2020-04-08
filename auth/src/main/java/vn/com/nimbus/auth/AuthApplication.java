package vn.com.nimbus.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import vn.com.nimbus.auth.config.AuthConfigLoader;
import vn.com.nimbus.common.BaseApplication;
import vn.com.nimbus.common.config.SetupConfig;
import vn.com.nimbus.common.model.constant.ServiceType;

import java.io.InputStream;

@SpringBootApplication
@ComponentScan(basePackages = {"vn.com.nimbus"})
public class AuthApplication extends BaseApplication {
    public static void main(String[] args) {
        loadCommonConfig();
        loadApiConfig(env);
        SpringApplication.run(AuthApplication.class, args);
    }

    private static void loadApiConfig(String env) {
        SetupConfig setupConfig = new SetupConfig();
        InputStream inputStreamConfig = setupConfig.serviceInputStream(env, AuthApplication.class, ServiceType.AUTH);
        AuthConfigLoader.getInstance().initializeConfigLoader(inputStreamConfig);
        setupConfig.configLogging(env);
    }
}
