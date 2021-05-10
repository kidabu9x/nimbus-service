package vn.com.nimbus.common.support.logging;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.slf4j.Marker;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class LoggingHandler {
    public static void preLog(Marker marker, ServerHttpRequest serverHttpRequest) {
        Map<String, String> headers = serverHttpRequest.getHeaders().toSingleValueMap();
        // as per RFC1945 the header is referer but it is not mandatory some implementations use referrer
        String referrer = headers.containsKey("referrer") ? headers.get("referrer") : headers.get("referer");
        referrer = referrer == null ? "referer" : referrer;
        Flux<DataBuffer> bufferBody = serverHttpRequest.getBody();

        String message = String.format("\"%s %s\" \"%s\" %s", serverHttpRequest.getMethod(), serverHttpRequest.getURI(), referrer,
                Optional.ofNullable(bufferBody).map(buffer -> buffer.toString()).orElse(""));
        doLog(marker, 0, message);

    }

    public static void postLog(Marker marker, ServerWebExchange serverWebExchange, long timestamp) {
        ServerHttpRequest serverHttpRequest = serverWebExchange.getRequest();
        Map<String, String> headers = serverHttpRequest.getHeaders().toSingleValueMap();

        int status = serverWebExchange.getResponse().getStatusCode().value();

        // as per RFC1945 the header is referer but it is not mandatory some implementations use referrer
        String referrer = headers.containsKey("referrer") ? headers.get("referrer") : headers.get("referer");
        String userAgent = headers.containsKey("user-agent") ? headers.get("user-agent") : headers.get("user-agent");
        referrer = referrer == null ? "referer" : referrer;
        userAgent = userAgent == null ? "user-agent" : userAgent;
        long elapsedTime = System.currentTimeMillis() - timestamp;

        // add to marker
        marker.add(Markers.append("elapsed_time", elapsedTime));
        marker.add(Markers.append("status_code", status));
        marker.add(Markers.append("user_agent", userAgent));

        String message = String.format("\"%s %s\" , status : %d , referer : \"%s\", user-agent :  \"%s\" , total time : %d ms",
                serverHttpRequest.getMethod(),
                serverHttpRequest.getURI(),
                status,
                referrer,
                userAgent,
                elapsedTime);

        doLog(marker, status, message);
    }

    private static void doLog(Marker marker, int status, String message) {
        if (status >= 400) {
            log.warn(marker, message);
        } else {
            log.info(marker, message);
        }
    }
}
