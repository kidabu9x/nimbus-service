package vn.com.nimbus.auth.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.filter.BaseFilter;
import vn.com.nimbus.common.support.logging.LoggingHandler;

import java.util.Map;


@Component
@Slf4j
public class ApiFilter extends BaseFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        Map<String, String> headers = serverWebExchange.getRequest().getHeaders().toSingleValueMap();
        String requestId = getRequestId(serverWebExchange, headers);

        Marker markers = addHeaderExtra(headers, serverWebExchange, requestId);
        long startTime = System.currentTimeMillis();
        serverWebExchange.getResponse()
                .getHeaders().setContentType(MediaType.APPLICATION_JSON);
        LoggingHandler.preLog(markers, serverWebExchange.getRequest());
        return webFilterChain.filter(serverWebExchange)
                .doOnSuccess(aVoid ->
                        LoggingHandler.postLog(markers, serverWebExchange, startTime)
                );
    }
}
