package vn.com.nimbus.common.exception;

import vn.com.nimbus.common.model.response.BaseResponseMessage;

public class AppExceptionCode {
    private int parentErrorCode;
    private String parentErrorMessage;
    private Integer subErrorCode;
    private String subErrorMessage;

    private AppExceptionCode(int parentErrorCode, String parentErrorMessage) {
        this.parentErrorCode = parentErrorCode;
        this.parentErrorMessage = parentErrorMessage;
    }

    private AppExceptionCode(int parentErrorCode, String parentErrorMessage, int subErrorCode, String subErrorMessage) {
        this.parentErrorCode = parentErrorCode;
        this.parentErrorMessage = parentErrorMessage;
        this.subErrorCode = subErrorCode;
        this.subErrorMessage = subErrorMessage;
    }

    public static vn.com.nimbus.common.exception.AppExceptionCode newErrorCode(BaseResponseMessage.Meta meta) {
        return new vn.com.nimbus.common.exception.AppExceptionCode(meta.getCode(), meta.getMessage());
    }

    public static vn.com.nimbus.common.exception.AppExceptionCode newErrorCode(int parentErrorCode, String parentErrorMessage) {
        return new vn.com.nimbus.common.exception.AppExceptionCode(parentErrorCode, parentErrorMessage);
    }

    public static vn.com.nimbus.common.exception.AppExceptionCode newErrorCode(int parentErrorCode, String parentErrorMessage, int subErrorCode, String subErrorMessage) {
        return new vn.com.nimbus.common.exception.AppExceptionCode(parentErrorCode, parentErrorMessage, subErrorCode, subErrorMessage);
    }


    private static final int CODE_OK = 200;
    private static final int CODE_CREATED = 201;
    private static final int CODE_ACCEPTED = 202;
    private static final int CODE_NO_CONTENT = 204;
    private static final int CODE_BAD_REQUEST = 400;
    private static final int CODE_UNAUTHORIZED = 401;
    private static final int CODE_FORBIDDEN = 403;
    private static final int CODE_NOT_FOUND = 404;
    private static final int CODE_INTERNAL_SERVER_ERROR = 500;

    /* All status code will be here */
    public static final vn.com.nimbus.common.exception.AppExceptionCode OK = newErrorCode(2001100, "Ok");

    public static final vn.com.nimbus.common.exception.AppExceptionCode CREATED = newErrorCode(2011100, "Created");

    public static final vn.com.nimbus.common.exception.AppExceptionCode ACCEPTED = newErrorCode(2021100, "Accepted");

    public static final vn.com.nimbus.common.exception.AppExceptionCode NO_CONTENT = newErrorCode(2041100, "No content");

    public static final vn.com.nimbus.common.exception.AppExceptionCode BAD_REQUEST = newErrorCode(4001100, "Bad request");
    public static final vn.com.nimbus.common.exception.AppExceptionCode INVALID_BILLS = newErrorCode(4001102, "Invalid Bill List");
    public static final vn.com.nimbus.common.exception.AppExceptionCode CUSTOMER_CODE_TOO_SHORT = newErrorCode(4001103, "Customer code too short. Min length 2");
    public static final vn.com.nimbus.common.exception.AppExceptionCode FIRST_OR_LAST_NAME_CANT_BE_EMPTY = newErrorCode(4001104, "first name or last name must be valid");
    public static final vn.com.nimbus.common.exception.AppExceptionCode BLOG_TITLE_TOO_LONG = newErrorCode(4001105, "Blog title is too long. Must be shorter than 255 characters");
    public static final vn.com.nimbus.common.exception.AppExceptionCode UNSUPPORTED_CONTENT_TYPE = newErrorCode(4001106, "Content type is unsupported");
    public static final vn.com.nimbus.common.exception.AppExceptionCode UNSUPPORTED_BLOG_STATUS = newErrorCode(4001107, "Content type is unsupported");
    public static final vn.com.nimbus.common.exception.AppExceptionCode FILE_UPLOADING_IS_EMPTY = newErrorCode(4001108, "File cannot be empty");
    public static final vn.com.nimbus.common.exception.AppExceptionCode UNSUPPORTED_IMAGE_FORMAT = newErrorCode(4001109, "Unsupported image format. Try again with following: '.png', '.jpg'");
    public static final vn.com.nimbus.common.exception.AppExceptionCode UNSUPPORTED_AUTH_TYPE = newErrorCode(4001110, "Unsupported auth type.");


    public static final vn.com.nimbus.common.exception.AppExceptionCode UN_AUTHORIZED = newErrorCode(4011100, "Unauthorized");
    public static final vn.com.nimbus.common.exception.AppExceptionCode INVALID_ACCESS_TOKEN = newErrorCode(4011100, "Unauthorized", 4011111, "Invalid access token");
    public static final vn.com.nimbus.common.exception.AppExceptionCode INVALID_API_KEY = newErrorCode(4011100, "Unauthorized", 4011112, "Invalid API-Key");
    public static final vn.com.nimbus.common.exception.AppExceptionCode AUTH_REQUIRED = newErrorCode(4011100, "Unauthorized", 4011113, "Auth required");
    public static final vn.com.nimbus.common.exception.AppExceptionCode PERMISSION_REQUIRED = newErrorCode(4011100, "Unauthorized", 4011113, "Permission required");
    public static final vn.com.nimbus.common.exception.AppExceptionCode PERMISSION_DENIED = newErrorCode(4011100, "Unauthorized", 4011113, "Permission denied");
    public static final vn.com.nimbus.common.exception.AppExceptionCode ACCOUNT_INFO_SOMETHING_WENT_WRONG = newErrorCode(4001127, "Technical errors from account information");
    public static final vn.com.nimbus.common.exception.AppExceptionCode CHECKOUT_SOMETHING_WENT_WRONG = newErrorCode(4001128, "Technical errors from checkout");

    public static final vn.com.nimbus.common.exception.AppExceptionCode BLOG_NOT_FOUND = newErrorCode(4041100, "Blog not found");
    public static final vn.com.nimbus.common.exception.AppExceptionCode USER_NOT_FOUND = newErrorCode(4041101, "User not found");
    public static final vn.com.nimbus.common.exception.AppExceptionCode BLOG_CONTENT_NOT_FOUND = newErrorCode(4041102, "Blog content not found");
    public static final vn.com.nimbus.common.exception.AppExceptionCode CATEGORY_NOT_FOUND = newErrorCode(4041103, "Category not found");
    public static final vn.com.nimbus.common.exception.AppExceptionCode SLUG_NOT_FOUND = newErrorCode(4041104, "Slug not found");

    public static final vn.com.nimbus.common.exception.AppExceptionCode EMAIL_HAS_EXISTS = newErrorCode(4091100, "email already exists");
    public static final vn.com.nimbus.common.exception.AppExceptionCode CATEGORY_HAS_EXISTS = newErrorCode(4091100, "category already exists");


    public static final vn.com.nimbus.common.exception.AppExceptionCode INTERNAL_SERVER_ERROR = newErrorCode(5001100, "Internal server error");
    public static final vn.com.nimbus.common.exception.AppExceptionCode FAIL_TO_CONVERT_FILE = newErrorCode(5001101, "Internal server error. Failed to convert file");
    public static final vn.com.nimbus.common.exception.AppExceptionCode FAIL_TO_UPLOAD_IMAGE = newErrorCode(5001102, "Fail to upload image");

    public int getHttpStatusCode() {
        if (this.parentErrorCode >= 1000000)
            return this.parentErrorCode / 10000;
        return this.parentErrorCode / 1000;
    }

    public int getParentErrorCode() {
        return parentErrorCode;
    }

    public String getParentErrorMessage() {
        return parentErrorMessage;
    }

    public Integer getSubErrorCode() {
        return subErrorCode;
    }

    public String getSubErrorMessage() {
        return subErrorMessage;
    }

}
