package vn.com.nimbus.common.model.response;


import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.common.data.domain.constant.PublicResponseType;

@Setter
@Getter
public class BasePublicResponse {

    private Integer id;

    private String slug;

    private String title;

    private PublicResponseType type;
}
