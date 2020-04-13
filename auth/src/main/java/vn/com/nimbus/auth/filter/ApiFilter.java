package vn.com.nimbus.auth.filter;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.exception.AppSecurityException;
import vn.com.nimbus.common.filter.BaseFilter;
import vn.com.nimbus.common.model.constant.KeyConstant;
import vn.com.nimbus.common.service.TokenService;
import vn.com.nimbus.common.support.logging.LoggingHandler;
import vn.com.nimbus.common.utils.AppUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;


@Component
@Slf4j
public class ApiFilter extends BaseFilter implements WebFilter {
    @Resource
    private TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        Map<String, String> headers = serverWebExchange.getRequest().getHeaders().toSingleValueMap();
        String requestId = getRequestId(serverWebExchange, headers);
        long startTime = System.currentTimeMillis();
        Marker markers = addHeaderExtra(headers, serverWebExchange, requestId);

        serverWebExchange.getResponse()
                .getHeaders().setContentType(MediaType.APPLICATION_JSON);
        LoggingHandler.preLog(markers, serverWebExchange.getRequest());

        String path = serverWebExchange.getRequest().getPath().toString();
        if (AppUtils.isBelongUrlWhiteList(path, KeyConstant.AUTH_WHITE_LIST_URL)) {
            return webFilterChain.filter(serverWebExchange).doOnSuccess(aVoid -> LoggingHandler.postLog(markers, serverWebExchange, startTime));
        }

        String accessToken = AppUtils.headerOption(headers, HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(accessToken))
            throw new AppSecurityException(AppExceptionCode.AUTH_REQUIRED);
        if (!accessToken.startsWith("Bearer ")) {
            throw new AppSecurityException(AppExceptionCode.AUTH_REQUIRED);
        }
        accessToken = accessToken.replaceFirst(KeyConstant.BEARER.concat(" "), "");
        Claims claims = tokenService.parseToken(accessToken);

        if (Objects.isNull(claims))
            throw new AppSecurityException(AppExceptionCode.INVALID_ACCESS_TOKEN);

        Integer userId = claims.get(KeyConstant.USER_ID, Integer.class);
        serverWebExchange.getResponse().getHeaders().add(KeyConstant.X_USER_ID, userId.toString());

        serverWebExchange.getResponse()
                .getHeaders().setContentType(MediaType.APPLICATION_JSON);
        LoggingHandler.preLog(markers, serverWebExchange.getRequest());
        return webFilterChain.filter(serverWebExchange)
                .doOnSuccess(aVoid ->
                        LoggingHandler.postLog(markers, serverWebExchange, startTime)
                );
    }
}
