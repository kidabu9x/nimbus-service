package vn.com.nimbus.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HachiumCategoryMappingID implements Serializable {
    private static final long serialVersionUID = -319420623579056554L;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "hachium_category_id")
    private Long hachiumCategoryId;
}
