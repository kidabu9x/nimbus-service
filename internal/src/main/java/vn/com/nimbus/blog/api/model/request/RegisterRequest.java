package vn.com.nimbus.blog.api.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class RegisterRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String avatar;

    @NotBlank
    private String type;
}
