package vn.com.nimbus.blog.internal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import vn.com.nimbus.blog.internal.config.ApiInternalConfigLoader;
import vn.com.nimbus.common.BaseApplication;
import vn.com.nimbus.common.config.SetupConfig;
import vn.com.nimbus.common.model.constant.ServiceType;

import java.io.InputStream;

@SpringBootApplication
@ComponentScan(basePackages = {"vn.com.nimbus"})
public class BlogInternalApplication extends BaseApplication {
    public static void main(String[] args) {
        loadCommonConfig();
        loadApiConfig(env);
        SpringApplication.run(BlogInternalApplication.class, args);
    }

    private static void loadApiConfig(String env) {
        SetupConfig setupConfig = new SetupConfig();
        InputStream inputStreamConfig = setupConfig.serviceInputStream(env, BlogInternalApplication.class, ServiceType.BLOG_INTERNAL);
        ApiInternalConfigLoader.getInstance().initializeConfigLoader(inputStreamConfig);
        setupConfig.configLogging(env);
    }
}
