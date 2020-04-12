package vn.com.nimbus.common.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class AuthRequest {
    private String type;

    private String token;

}
