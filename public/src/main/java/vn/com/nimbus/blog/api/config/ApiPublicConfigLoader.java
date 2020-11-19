package vn.com.nimbus.blog.api.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ApiPublicConfigLoader {
    private static ApiPublicConfigLoader ourInstance = new ApiPublicConfigLoader();

    public static ApiPublicConfigLoader getInstance() {
        return ourInstance;
    }

    private ApiPublicConfigLoader() {
    }

    public static ApiPublicConfig apiInternalConfig;

    public synchronized void initializeConfigLoader(InputStream inputStreamConfig) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            JsonNode configJsonNode = mapper.readTree(inputStreamConfig);
            apiInternalConfig = mapper.treeToValue(configJsonNode, ApiPublicConfig.class);
        } catch (IOException e) {
            log.error("Error Init Api config loader : {} ", e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
