package vn.com.nimbus.auth.service;

import vn.com.nimbus.auth.model.request.OauthRequest;
import vn.com.nimbus.auth.model.response.AuthResponse;
import vn.com.nimbus.auth.model.response.ProfileResponse;

public interface AuthService {
    AuthResponse oauth(OauthRequest request);

    ProfileResponse getProfile(Long id);
}
