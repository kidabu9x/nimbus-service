package vn.com.nimbus.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "hachium_course")
@Getter
@Setter
public class HachiumCourse implements Serializable {

    private static final long serialVersionUID = 2124761624754033749L;
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", unique = true)
    private String url;

    @Column(name = "title", unique = true)
    private String title;

    @Column(name = "image", unique = true)
    private String image;

    @Column(name = "old_price", unique = true)
    private Long oldPrice;

    @Column(name = "price", unique = true)
    private Long price;

    @Column(name = "author", unique = true)
    private String author;

    @Column(name = "hachium_category_id", unique = true)
    private Long hachiumCategoryId;
}
