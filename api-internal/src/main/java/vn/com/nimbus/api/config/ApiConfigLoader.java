package vn.com.nimbus.api.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ApiConfigLoader {
    private static ApiConfigLoader ourInstance = new ApiConfigLoader();

    public static ApiConfigLoader getInstance() {
        return ourInstance;
    }

    private ApiConfigLoader() {
    }

    public static ApiConfig apiConfig;

    public synchronized void initializeConfigLoader(InputStream inputStreamConfig) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            JsonNode configJsonNode = mapper.readTree(inputStreamConfig);
            apiConfig = mapper.treeToValue(configJsonNode, ApiConfig.class);
        } catch (IOException e) {
            log.error("Error Init Api config loader : {} ", e.getMessage());
        }
    }
}
