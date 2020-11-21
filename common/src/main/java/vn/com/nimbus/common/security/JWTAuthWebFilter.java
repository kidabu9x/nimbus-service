package vn.com.nimbus.common.security;

import net.logstash.logback.marker.Markers;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.logging.LoggingHandler;

public class JWTAuthWebFilter implements WebFilter {

    private static final ServerAuthenticationSuccessHandler authSuccessHandler = new WebFilterChainServerAuthenticationSuccessHandler();

    private AuthUtil jwtUtil;
    private AuthenticationProvider authenticationProvider;

    JWTAuthWebFilter(AuthUtil jwtUtil, AuthenticationProvider authenticationProvider) {
        this.jwtUtil = jwtUtil;
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.just(exchange)
                .flatMap(matchResult -> auth(exchange))
                .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
                .flatMap(token -> authenticate(exchange, chain, token));
    }

    private Mono<Void> authenticate(ServerWebExchange exchange, WebFilterChain chain, Authentication authentication) {
        // Put authentication to security context
        var webFilterExchange = new WebFilterExchange(exchange, chain);
        var securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        // Init log marker, it shouldn't be here, but let's put it here fore now
        // FIXME: Use Reactive Context here because Reactive code flows aren't always in the same thread
        var markerMap = LoggingHandler.preLog(exchange.getRequest(), authentication);

        markerMap.forEach(MDC::put);
        var startTime = System.currentTimeMillis();
        var marker = Markers.appendEntries(markerMap);

        return authSuccessHandler
                .onAuthenticationSuccess(webFilterExchange, authentication)
                .subscriberContext(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                .doOnSuccess(v -> LoggingHandler.postLog(marker, exchange, startTime));
    }

    private Mono<Authentication> auth(ServerWebExchange serverWebExchange) {
        var auth = jwtUtil.auth(serverWebExchange, authenticationProvider);
        return auth == null ? Mono.empty() : Mono.just(auth);
    }
}
