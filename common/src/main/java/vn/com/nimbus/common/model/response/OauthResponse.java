package vn.com.nimbus.common.model.response;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.common.model.constant.KeyConstant;

@Setter
@Getter
public class OauthResponse {
    private String accessToken;

    private String tokenType = KeyConstant.BEARER;
}
