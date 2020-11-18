package vn.com.nimbus.common.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import vn.com.nimbus.common.model.error.ErrorCode;
import vn.com.nimbus.common.model.exception.BaseException;

import java.io.IOException;

@Slf4j
public class JsonParseService<V> {
    public V toEntityData(String data, Class<V> clazz) {
        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            log.error("ERROR PARSE JSON OBJECT {}", data);
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

}
