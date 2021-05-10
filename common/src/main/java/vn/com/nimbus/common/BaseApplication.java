package vn.com.nimbus.common;

import vn.com.nimbus.common.config.SetupConfig;

public class BaseApplication {

    protected static String env;

    protected static void loadCommonConfig() {
        SetupConfig setupConfig = new SetupConfig();
        env = setupConfig.getEnv();
        setupConfig.setupCommonConfig(env);
    }
}
