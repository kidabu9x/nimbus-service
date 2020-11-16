package vn.com.nimbus.blog.internal.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ApiInternalConfigLoader {
    private static ApiInternalConfigLoader ourInstance = new ApiInternalConfigLoader();

    public static ApiInternalConfigLoader getInstance() {
        return ourInstance;
    }

    private ApiInternalConfigLoader() {
    }

    public static ApiInternalConfig apiInternalConfig;

    public synchronized void initializeConfigLoader(InputStream inputStreamConfig) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            JsonNode configJsonNode = mapper.readTree(inputStreamConfig);
            apiInternalConfig = mapper.treeToValue(configJsonNode, ApiInternalConfig.class);
        } catch (IOException e) {
            log.error("Error Init Api config loader : {} ", e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
