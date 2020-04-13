package vn.com.nimbus.common.service;

import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.request.AuthRequest;
import vn.com.nimbus.common.model.request.RegisterRequest;
import vn.com.nimbus.common.model.response.OauthResponse;
import vn.com.nimbus.common.model.response.ProfileResponse;

public interface AuthService {

    Mono<OauthResponse> oauth(AuthRequest request);

    Mono<OauthResponse> register(RegisterRequest request);

    Mono<ProfileResponse> getProfile(Integer userId);

}
