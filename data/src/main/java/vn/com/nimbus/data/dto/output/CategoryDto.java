package vn.com.nimbus.data.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.com.nimbus.data.domain.Category;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class CategoryDto implements Serializable {
    private static final long serialVersionUID = 4422941184991998658L;
    private Category category;
    private String slug;
}
