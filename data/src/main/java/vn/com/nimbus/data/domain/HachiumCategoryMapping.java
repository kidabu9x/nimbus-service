package vn.com.nimbus.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "hachium_category_mapping")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HachiumCategoryMapping implements Serializable {
    private static final long serialVersionUID = 7845134371385763149L;
    @EmbeddedId
    private HachiumCategoryMappingID id;
}
