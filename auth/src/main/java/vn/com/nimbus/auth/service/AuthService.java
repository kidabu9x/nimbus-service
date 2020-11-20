package vn.com.nimbus.auth.service;

import vn.com.nimbus.auth.model.request.OauthRequest;
import vn.com.nimbus.auth.model.response.AuthResponse;

public interface AuthService {
    AuthResponse oauth(OauthRequest request);
}
