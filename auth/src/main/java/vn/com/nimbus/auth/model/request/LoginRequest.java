package vn.com.nimbus.auth.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1124334515243459788L;

    private String username;
    private String password;
}
