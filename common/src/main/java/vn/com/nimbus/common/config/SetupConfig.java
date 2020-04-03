package vn.com.nimbus.common.config;

import java.io.InputStream;
import java.util.Optional;

public class SetupConfig {
    public void setupCommonConfig(String env) {
        InputStream inputStreamConfig = commonInputStream(env);
        ConfigLoader.getInstance().initializeConfigLoader(inputStreamConfig);
    }

    public void configLogging(String env) {
        if (env.equals("DEPLOY"))
            System.setProperty("logging.config", "logging/logback-" + env + ".xml");
    }

    private InputStream commonInputStream(String env) {
        String configPath = "config/" + env + "-config.json";
        return getClass().getResourceAsStream("/".concat(configPath));
    }

    public InputStream serviceInputStream(String env, Class clazz, String serviceType) {
        String configPath = "config/" + env + "-" + serviceType + "-config.json";
        return clazz.getResourceAsStream("/".concat(configPath));
    }

    public String getEnv() {
        return Optional.ofNullable(System.getProperty("env")).orElseGet(() -> "local");
    }
}
