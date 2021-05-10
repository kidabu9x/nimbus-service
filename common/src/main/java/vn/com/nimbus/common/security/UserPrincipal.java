package vn.com.nimbus.common.security;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserPrincipal {
    private Long id;
    private String email;
}