package com.ShopMe.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter @Getter
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, length = 256, nullable = false)
    private String name;
    @Column(unique = true, length = 256, nullable = false)
    private String alias;
    @Column(length = 512, nullable = false, name = "short_description")
    private String shortDescription;
    @Column(length = 4000, nullable = false,  name = "full_description")
    private String fullDescription;

    @Column(name = "created_time")
    private Date createdTime;
    private Date updatedTime;

    private boolean enabled;

    @Column(name = "in_stock")
    private boolean inStock;

    private float cost;

    private float price;
    @Column(name = "discount_percent")
    private float discountPercent;

    private float length;
    private float width;
    private float height;
    private float weight;

    @Column(name = "main_image", nullable = false)
    private String mainImage;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; // one cate. can have many products
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand; // one brand can have many products
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductImage> images = new HashSet<>();  //one product can have many images

    public void addExtraImage(String imageName){
        this.images.add(new ProductImage(imageName, this)); // image added to this Product only
    }

    @Transient
    public String getMainImagePath(){
        if(id == null || mainImage == null) {
            return "/images/image-thumbnail.png";
        }
        return "/product-images/" + this.id + "/" + this.mainImage;
    }
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
