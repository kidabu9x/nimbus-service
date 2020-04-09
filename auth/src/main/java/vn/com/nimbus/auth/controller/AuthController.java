package vn.com.nimbus.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.controller.AbstractController;
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

    @PostMapping("/register")
    public Mono<BaseResponse> register(@Valid @RequestBody RegisterRequest request) {
        return processBaseResponse(authService.register(request));
    }

    @PostMapping("/login")
    public Mono<BaseResponse> login() {
        return processBaseResponse();
    }
}
