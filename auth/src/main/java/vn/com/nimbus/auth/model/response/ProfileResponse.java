package vn.com.nimbus.auth.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProfileResponse implements Serializable {
    private static final long serialVersionUID = -870178829644960817L;

    private Long id;
    private String email;
    private String avatar;
    private String firstName;
    private String lastName;
}
