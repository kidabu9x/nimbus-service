package vn.com.nimbus.common.model.dto;

import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.data.domain.constant.SlugPoolType;

import java.io.Serializable;

@Getter
@Setter
public class SlugPoolDto implements Serializable {
    private static final long serialVersionUID = -3094792591227046430L;

    private String title;
    private SlugPoolType type;
    private Long targetId;
}
