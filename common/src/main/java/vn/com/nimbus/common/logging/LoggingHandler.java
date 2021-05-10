package vn.com.nimbus.common.logging;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Marker;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import vn.com.nimbus.common.model.constant.KeyConstant;
import vn.com.nimbus.common.security.UserPrincipal;
import vn.com.nimbus.common.utils.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class LoggingHandler {

    private LoggingHandler() {
    }

    public static Map<String, String> preLog(ServerHttpRequest serverHttpRequest, Authentication authentication) {
        var headers = serverHttpRequest.getHeaders().toSingleValueMap();
        var markerMap = initMarkerMap(headers);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        markerMap.put(KeyConstant.USER_ID, userPrincipal.getId().toString());

        return markerMap;
    }

    public static void postLog(Marker marker, ServerWebExchange serverWebExchange, long startTime) {
        ServerHttpRequest serverHttpRequest = serverWebExchange.getRequest();
        Map<String, String> headers = serverHttpRequest.getHeaders().toSingleValueMap();

        int status = Objects.requireNonNull(serverWebExchange.getResponse().getStatusCode()).value();

        String userAgent = Util.getInsensitiveValue(headers, KeyConstant.USER_AGENT);
        userAgent = userAgent == null ? "default user agent" : userAgent;
        long elapsedTime = System.currentTimeMillis() - startTime;

        // add to marker
        marker.add(Markers.append("elapsed_time", elapsedTime));
        marker.add(Markers.append("status_code", status));
        marker.add(Markers.append("user_agent", userAgent));

        var message = String.format("Done %s to %s with status %s in %s ms",
                serverHttpRequest.getMethod(),
                serverHttpRequest.getURI(),
                status,
                elapsedTime);
        doLog(marker, status, message);

        if (elapsedTime > KeyConstant.LONG_RUN_REQUEST) {
            log.info("Execution time exceed expectation: {} ms", elapsedTime);
        }
    }

    private static Map<String, String> initMarkerMap(Map<String, String> headers) {
        String requestId = Util.getInsensitiveValue(headers, KeyConstant.X_REQUEST_ID);
        String clientIp = Util.getInsensitiveValue(headers, KeyConstant.CF_CONNECTING_IP);
        if (Strings.isBlank(requestId)) {
            requestId = Util.generateRequestId();
        }

        Map<String, String> headerMark = new HashMap<>();

        headerMark.put(KeyConstant.X_REQUEST_ID, requestId);
        headerMark.put(KeyConstant.CF_CONNECTING_IP, clientIp);

        return headerMark;
    }

    private static void doLog(Marker marker, int status, String message) {
        if (status >= 500) {
            log.error(marker, message);
        } else if (status >= 400) {
            log.warn(marker, message);
        } else {
            log.info(marker, message);
        }
    }
}
