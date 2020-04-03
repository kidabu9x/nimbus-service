package vn.com.nimbus.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import vn.com.nimbus.api.config.ApiConfigLoader;
import vn.com.nimbus.common.BaseApplication;
import vn.com.nimbus.common.config.SetupConfig;
import vn.com.nimbus.common.constant.ServiceType;

import java.io.InputStream;

@SpringBootApplication
@ComponentScan(basePackages = {"vn.com.nimbus"})
public class ApiApplication extends BaseApplication {
    public static void main(String[] args) {
        loadCommonConfig();
        loadApiConfig(env);
        SpringApplication.run(ApiApplication.class, args);
    }

    private static void loadApiConfig(String env) {
        SetupConfig setupConfig = new SetupConfig();
        InputStream inputStreamConfig = setupConfig.serviceInputStream(env, ApiApplication.class, ServiceType.API);
        ApiConfigLoader.getInstance().initializeConfigLoader(inputStreamConfig);
        setupConfig.configLogging(env);
    }
}
