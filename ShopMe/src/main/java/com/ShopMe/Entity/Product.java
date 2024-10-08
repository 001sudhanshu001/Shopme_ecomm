package com.ShopMe.Entity;

import com.ShopMe.UtilityClasses.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Setter @Getter
@Table(name = "products",
        indexes = {
        @Index(name = "product_name_index", columnList = "name", unique = true),
        @Index(name = "product_alias_index", columnList = "alias", unique = true)
        }
    )
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 256, nullable = false, unique = true)
    private String name;

    @Column(length = 256, nullable = false, unique = true)
    private String alias;

    @Column(length = 2000, nullable = false, name = "short_description")
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
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetails> details = new ArrayList<>();

    private int reviewCount;
    private float averageRating;

    @Transient
    private String preSignedURLForMainImage;

    public void addDetails(String name, String value){
        this.details.add(new ProductDetails(name, value, this));
    }

    public void addDetails(Integer id, String name, String value){
        this.details.add(new ProductDetails(id,name, value, this));
    }

    @Transient
    public void addExtraImage(String imageName){
        this.images.add(new ProductImage(imageName, this));
    }

    @Transient
    public String getMainImagePath(){
        if(id == null || mainImage == null) {
            return "/images/image-thumbnail.png";
        }
      //  return "/product-images/" + this.id + "/" + this.mainImage;

        return Constants.S3_BASE_URI + "/product-images/" + this.id + "/" + this.mainImage;

    }
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public boolean containsImageName(String imageName) {

        for (ProductImage img : images) {
            if (img.getName().equals(imageName)) {
                return true;
            }
        }
        return false;
    }

    @Transient
    public String getShortName() {
        // Magic Number
        int maxShortNameLengthWillBe = 70;
        if(this.name.length() > maxShortNameLengthWillBe){
            return name.substring(0, maxShortNameLengthWillBe) + "...";
        }
        return this.name;
    }

    @Transient
    public float getDiscountPrice() {
        if(discountPercent > 0){
            return price * ((100 - discountPercent)/100);
        }
        return this.price;
    }

    public Product(Integer id) {
        this.id = id;
    }
}
