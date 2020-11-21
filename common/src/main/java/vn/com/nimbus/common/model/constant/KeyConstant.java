package vn.com.nimbus.common.model.constant;

import java.util.Collections;
import java.util.List;

public interface KeyConstant {
    String X_REQUEST_ID = "X-Request-ID";
    String X_DEVICE_ID = "X-Device-ID";
    String X_DEVICE_SESSION_ID = "X-Device-Session-ID";
    String X_USER_ID = "X_User_ID";
    List<String> AUTH_WHITE_LIST_URL = Collections.singletonList("/oauth2");
    String X_DEVICE_OS = "X-Device-OS";
    String X_DEVICE_VERSION = "X-BillPay-Version";

    String BEARER = "Bearer";
    String USER_ID = "user_id";
    String EMAIL = "email";
    String USER_AGENT = "User-Agent";
    String CF_CONNECTING_IP = "CF-Connecting-IP";
    int LONG_RUN_REQUEST = 2_000;
}
