package vn.com.nimbus.common.model.error;

import org.springframework.http.HttpStatus;

public class ErrorCode {
    public static final BusinessErrorCode INTERNAL_SERVER_ERROR = new BusinessErrorCode(
            500_000, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final BusinessErrorCode THIRD_PARTY_ERROR = new BusinessErrorCode(
            500_001, "Third party is not available", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final BusinessErrorCode INVALID_PARAMETERS = new BusinessErrorCode(
            400_000, "Invalid parameters", HttpStatus.BAD_REQUEST);
    public static final BusinessErrorCode FORBIDDEN = new BusinessErrorCode(
            403_000, "Forbidden", HttpStatus.FORBIDDEN);
    public static final BusinessErrorCode RESOURCE_NOT_FOUND = new BusinessErrorCode(
            404_000, "Resource not found", HttpStatus.NOT_FOUND);

    private ErrorCode() {
    }
}
