package com.ShopMe.Entity;

import com.ShopMe.UtilityClasses.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "product_images")
public class ProductImage {  // this is for extra images of the product
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // one product can have many images

    @Transient
    private String preSignedURL;

    public ProductImage(String name, Product product) {
        this.name = name;
        this.product = product;
    }

    public ProductImage(Integer id, String name, Product product) {
        this.id = id;
        this.name = name;
        this.product = product;
    }

    @Transient
    public String getImagePath() {
       // return "/product-images/" + product.getId() + "/extras/" + this.name;

        return Constants.S3_BASE_URI + "/product-images/" + product.getId() + "/extras/" + this.name;
    }

}
