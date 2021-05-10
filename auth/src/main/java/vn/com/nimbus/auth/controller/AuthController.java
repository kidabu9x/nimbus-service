package vn.com.nimbus.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import vn.com.nimbus.auth.model.request.OauthRequest;
import vn.com.nimbus.auth.model.response.AuthResponse;
import vn.com.nimbus.auth.model.response.ProfileResponse;
import vn.com.nimbus.auth.service.AuthService;
import vn.com.nimbus.common.model.response.BaseResponse;
import vn.com.nimbus.common.permission.Permissions;

import javax.validation.Valid;

@RestController
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/oauth")
    public Mono<BaseResponse<AuthResponse>> login(@Valid @RequestBody OauthRequest request) {
        return Mono
                .just(authService.oauth(request))
                .map(BaseResponse::ofSucceeded);
    }

    @GetMapping("/profile")
    public Mono<BaseResponse<ProfileResponse>> getProfile() {
        return Permissions.getCurrentUser()
                .map(u -> authService.getProfile(u.getId())).map(BaseResponse::ofSucceeded);
    }
}
