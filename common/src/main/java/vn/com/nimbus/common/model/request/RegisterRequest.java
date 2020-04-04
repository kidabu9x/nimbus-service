package vn.com.nimbus.common.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class RegisterRequest {
    @NotEmpty
    @Email
    private String email;

    private String firstName;

    private String lastName;

    private String avatar;
}
