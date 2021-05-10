package vn.com.nimbus.common.config;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ConfigLoader {

    private static ConfigLoader ourInstance = new ConfigLoader();

    public static ConfigLoader getInstance() {
        if (ourInstance == null)
            ourInstance = new ConfigLoader();

        return ourInstance;
    }

    private ConfigLoader() {
    }

    public ConfigData configData;

    public synchronized void initializeConfigLoader(InputStream inputStreamConfig) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            JsonNode configJsonNode = mapper.readTree(inputStreamConfig);
            configData = mapper.treeToValue(configJsonNode, ConfigData.class);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error to load config data. ex {}", e.getMessage());
        }
    }
}
