package vn.com.nimbus.common.filter;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Marker;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import vn.com.nimbus.common.model.constant.KeyConstant;
import vn.com.nimbus.common.utils.AppUtils;
import vn.com.nimbus.common.utils.UUIDUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BaseFilter {
    protected String getRequestId(ServerWebExchange serverWebExchange, Map<String, String> headers) {
        serverWebExchange.getResponse()
                .getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String requestId = AppUtils.headerOption(headers, KeyConstant.X_REQUEST_ID);
        if (Strings.isBlank(requestId)) {
            requestId = UUIDUtils.generateUUID();
        }
        return requestId;
    }

    protected Marker addHeaderExtra(Map<String, String> headers, ServerWebExchange serverWebExchange, String requestId) {
        String deviceId = AppUtils.headerOption(headers, KeyConstant.X_DEVICE_ID);

        if (Strings.isBlank(deviceId)) {
            deviceId = KeyConstant.X_DEVICE_ID;
        }
        String deviceSessionId = AppUtils.headerOption(headers, KeyConstant.X_DEVICE_SESSION_ID);
        if (Strings.isBlank(deviceSessionId)) {
            deviceSessionId = UUIDUtils.generateUUID();
        }
        String appVersion = AppUtils.headerOption(headers, KeyConstant.X_DEVICE_VERSION);
        String deviceOS = AppUtils.headerOption(headers, KeyConstant.X_DEVICE_OS);
        if (Strings.isBlank(appVersion) || Strings.isBlank(deviceOS)) {
            appVersion = "ORIGINAL_VERSION";
            deviceOS = "ORIGINAL_OS";
        }

        Map<String, String> headerMark = new HashMap<>();
        headerMark.put(KeyConstant.X_REQUEST_ID, requestId);


        Marker marker = Markers.appendEntries(headerMark);

        buildHeaderResponse(serverWebExchange, requestId, deviceId, deviceSessionId, appVersion, deviceOS);
        return marker;
    }


    private void buildHeaderResponse(ServerWebExchange serverWebExchange, String requestId, String deviceId, String deviceSessionId, String appVersion, String deviceOS) {
        serverWebExchange.getResponse().getHeaders().add(KeyConstant.X_REQUEST_ID, requestId);
        serverWebExchange.getResponse().getHeaders().add(KeyConstant.X_DEVICE_ID, deviceId);
        serverWebExchange.getResponse().getHeaders().add(KeyConstant.X_DEVICE_SESSION_ID, deviceSessionId);
        serverWebExchange.getResponse().getHeaders().add(KeyConstant.X_DEVICE_VERSION, appVersion);
        serverWebExchange.getResponse().getHeaders().add(KeyConstant.X_DEVICE_OS, deviceOS);
    }
}
