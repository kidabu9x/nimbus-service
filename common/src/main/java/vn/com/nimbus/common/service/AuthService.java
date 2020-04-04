package vn.com.nimbus.common.service;

import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.request.RegisterRequest;

public interface AuthService {

    Mono<Object> register(RegisterRequest request);
}
