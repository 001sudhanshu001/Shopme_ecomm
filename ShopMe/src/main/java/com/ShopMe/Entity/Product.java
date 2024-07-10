package com.ShopMe.Entity;

import com.ShopMe.UtilityClasses.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Setter @Getter
@Table(name = "products")
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, length = 256, nullable = false)
    private String name;
    @Column(unique = true, length = 256, nullable = false)
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
    private Category category; // one cate. can have many products

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand; // one brand can have many products

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();  //one product can have many images

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetails> details = new ArrayList<>();

    private int reviewCount;
    private float averageRating;


    public void addDetails(String name, String value){
        this.details.add(new ProductDetails(name, value, this));
    }

    public void addDetails(Integer id, String name, String value){
        this.details.add(new ProductDetails(id,name, value, this));
    }

    @Transient
    public void addExtraImage(String imageName){
        this.images.add(new ProductImage(imageName, this)); // image added to this Product only
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
        Iterator<ProductImage> iterator = images.iterator();

        while (iterator.hasNext()){
            ProductImage img = iterator.next();
            if(img.getName().equals(imageName)){
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
