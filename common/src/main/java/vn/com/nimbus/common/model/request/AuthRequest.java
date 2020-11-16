package vn.com.nimbus.common.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {
    private String type;

    private String token;

}
