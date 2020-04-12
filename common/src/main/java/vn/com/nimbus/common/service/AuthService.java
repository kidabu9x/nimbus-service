package vn.com.nimbus.common.service;

import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.request.AuthRequest;

public interface AuthService {

    Mono<Object> oauth(AuthRequest request);
}
