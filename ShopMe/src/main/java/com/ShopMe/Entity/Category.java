package com.ShopMe.Entity;

import com.ShopMe.UtilityClasses.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "categories",
        indexes = {
              @Index(name = "category_name_index", columnList = "name", unique = true),
              @Index(name = "category_alias_index", columnList = "alias", unique = true)
        }
)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, nullable = false , unique = true)
    private String name;

    @Column(length = 64, nullable = false , unique = true)
    private String alias;

    @Column(length = 128, nullable = false) // length of image file
    private String image;

    private boolean enabled;

    // This is for Searching Category
    @Column(name = "all_parent_ids", length = 256, nullable = true)// "nullable = true" for Root Category
    private String allParentIDs;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("name asc")
    private Set<Category> children = new HashSet<>();

    @Transient
    private String preSignedURL;

    public static Category copyIdAndName(Category category){
        Category cat = new Category();
        cat.setName(category.getName());
        cat.setId(category.getId());

        return cat;
    }

    public static Category copyIdAndName(Integer id,String name){
        Category copyCategory = new Category();
        copyCategory.setName(name);
        copyCategory.setId(id);

        return copyCategory;
    }

    public static Category copyFull(Category category){
        Category copyCategory = new Category();
        copyCategory.setName(category.getName());
        copyCategory.setId(category.getId());
        copyCategory.setImage(category.getImage());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setEnabled(category.isEnabled());
        copyCategory.setHasChildren(category.getChildren().size() > 0);

        return copyCategory;
    }

    public static Category copyFull(Category category, String name){
         Category copyCategory = Category.copyFull(category);
         copyCategory.setName(name);

         return copyCategory;
    }

    public Category(String name){
        this.name = name;
        this.alias = name;
        this.image = "default.png";
    }

    public Category(Integer id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    @Transient
    public String getImagePath() {
        if(this.id == null){
            return "/images/image-thumbnail.png";
        }

        // return "/category-images/" + this.id + "/" + this.image;
        return Constants.S3_BASE_URI + "/category-images/" + this.id + "/" + this.image;

    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren){
        this.hasChildren = hasChildren;
    }

    @Transient // so that column will not be created
    private boolean hasChildren;

    @Override
    public String toString(){
        return this.name;
    }

}
