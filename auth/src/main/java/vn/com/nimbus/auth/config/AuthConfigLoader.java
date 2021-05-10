package vn.com.nimbus.auth.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class AuthConfigLoader {
    private static AuthConfigLoader ourInstance = new AuthConfigLoader();

    public static AuthConfigLoader getInstance() {
        return ourInstance;
    }

    private AuthConfigLoader() {
    }

    public static AuthConfig authConfig;

    public synchronized void initializeConfigLoader(InputStream inputStreamConfig) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            JsonNode configJsonNode = mapper.readTree(inputStreamConfig);
            authConfig = mapper.treeToValue(configJsonNode, AuthConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error Init Api config loader : {} ", e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
