package vn.com.nimbus.common.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

public class SetupConfig {
    private final String DEPLOY = "DEPLOY";

    public void setupCommonConfig(String env) {
        InputStream inputStreamConfig = commonInputStream(env);
        ConfigLoader.getInstance().initializeConfigLoader(inputStreamConfig);
    }

    public void configLogging(String env) {
        String mode = this.getMode();
        if (mode.equals(DEPLOY))
            System.setProperty("logging.config", "logging/logback-" + env + ".xml");
    }

    private InputStream commonInputStream(String env) {
        String mode = this.getMode();
        String configPath = "config/" + env + "-config.json";
        if (mode.equals(DEPLOY)) {
            try {
                return new FileInputStream(configPath);
            } catch (FileNotFoundException e) {
                return null;
            }
        } else {
            return getClass().getResourceAsStream("/".concat(configPath));
        }
    }

    public InputStream serviceInputStream(String env, Class clazz, String serviceType) {
        String mode = this.getMode();
        String configPath = "config/" + env + "-" + serviceType + "-config.json";
        if (mode.equals(DEPLOY)) {
            try {
                return new FileInputStream(configPath);
            } catch (FileNotFoundException e) {
                return null;
            }
        } else {
            return clazz.getResourceAsStream("/".concat(configPath));
        }
    }

    public String getEnv() {
        return this.getEnv("env", "local");
    }

    public String getMode() {
        return this.getEnv("mode", "");
    }

    private String getEnv(String mode, String s) {
        return Optional.ofNullable(System.getProperty(mode)).orElseGet(() -> s);
    }
}
