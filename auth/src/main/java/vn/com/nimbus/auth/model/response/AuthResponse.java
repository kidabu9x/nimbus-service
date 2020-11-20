package vn.com.nimbus.auth.model.response;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.common.model.constant.KeyConstant;

@Getter
@Setter
public class AuthResponse {
    private String accessToken;

    private String tokenType = KeyConstant.BEARER;
}
