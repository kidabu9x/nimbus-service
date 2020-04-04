package vn.com.nimbus.common.model.constant;

import java.util.Arrays;
import java.util.List;

public interface KeyConstant {
    String X_REQUEST_ID = "X-Request-ID";
    String X_DEVICE_ID = "X-Device-ID";
    String X_DEVICE_SESSION_ID = "X-Device-Session-ID";
    String X_USER_ID = "X_User_ID";
    String CURRENT_CONTEXT = "CURRENT_CONTEXT";
    String X_API_KEY = "X-API-KEY";
    String X_API_SECRET = "X-Api-Secret";
    List<String> INTERNAL_WHITE_LIST_URL = Arrays.asList("/payment-notify-order","/health-check", "/migrate_prepaid", "/login");
    List<String> MOBILE_WHITE_LIST_URL = Arrays.asList("/health-check");
    List<String> OPS_WHITE_LIST_URL = Arrays.asList("/health-check");
    String X_DEVICE_OS = "X-Device-OS";
    String X_DEVICE_VERSION = "X-BillPay-Version";
    String SERVICE_CODE = "BILL_MANAGEMENT";

}
