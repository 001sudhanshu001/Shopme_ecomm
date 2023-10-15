package com.ShopMe.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 45, unique = true)
    private String name;
    @Column(nullable = false, length = 128)
    private String logo;

    @ManyToMany
    @JoinTable(
            name = "brands_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
            )
    private Set<Category> categories = new HashSet<>();

    @Transient
    public String getLogoPath(){
        if(this.id == null) {
            return "/images/image-thumbnail.png";
        }

        return "/images/brandlogos/" + this.id + "/" + this.logo;
    }


    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
