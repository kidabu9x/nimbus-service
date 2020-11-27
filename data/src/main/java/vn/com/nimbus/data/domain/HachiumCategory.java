package vn.com.nimbus.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "hachium_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HachiumCategory implements Serializable {
    private static final long serialVersionUID = 2271175072081576670L;
    @Id
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "url", unique = true)
    private String url;

    @Column(name = "title", unique = true)
    private String title;
}
