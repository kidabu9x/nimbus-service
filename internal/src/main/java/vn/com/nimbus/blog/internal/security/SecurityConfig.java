package vn.com.nimbus.blog.internal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.security.AuthUtil;
import vn.com.nimbus.common.security.AuthenticationProvider;
import vn.com.nimbus.common.security.JWTAuthWebFilter;

import javax.annotation.Resource;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Resource
    private AuthUtil jwtUtil;

    @Resource
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                .and()
                .addFilterAt(new JWTAuthWebFilter(jwtUtil, authenticationProvider), SecurityWebFiltersOrder.FIRST)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/health").permitAll()
                // Permit swagger
                .pathMatchers("/v2/api-docs", "/oauth", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .build();
    }
}
