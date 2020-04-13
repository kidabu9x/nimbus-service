package vn.com.nimbus.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import vn.com.nimbus.common.controller.AbstractController;
import vn.com.nimbus.common.model.constant.KeyConstant;
import vn.com.nimbus.common.model.request.AuthRequest;
import vn.com.nimbus.common.model.request.RegisterRequest;
import vn.com.nimbus.common.model.response.BaseResponse;
import vn.com.nimbus.common.service.AuthService;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("v1")
@Slf4j
public class AuthController extends AbstractController {
    @Resource
    private AuthService authService;

    @PostMapping("/oauth2-deprecated")
    public Mono<BaseResponse> auth(@Valid @RequestBody AuthRequest request) {
        return processBaseResponse(authService.oauth(request));
    }

    @PostMapping("/oauth2")
    public Mono<BaseResponse> login(@Valid @RequestBody RegisterRequest request) {
        return processBaseResponse(authService.register(request));
    }

    @GetMapping("/profile")
    public Mono<BaseResponse> getProfile(ServerHttpResponse currentResponse) {
        HttpHeaders headers = currentResponse.getHeaders();
        String userId = headers.getFirst(KeyConstant.X_USER_ID);
        assert userId != null;
        return processBaseResponse(authService.getProfile(Integer.valueOf(userId)));
    }
}
