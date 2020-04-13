package vn.com.nimbus.common.model.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfileResponse {
    private Integer id;

    private String email;

    private String firstName;

    private String lastName;

    private String avatar;
}
