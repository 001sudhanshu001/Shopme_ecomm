package com.ShopMe.DAO;

import com.ShopMe.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepo extends JpaRepository<Product, Integer> {

    Product findByName(String name);

    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    Long countById(Integer id); // while deleting we will check if any Product with this id exists or not

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% " +
            "OR p.id LIKE %?1%" +
            "OR p.shortDescription LIKE %?1%" +
            "OR p.fullDescription LIKE %?1%" +
            "OR p.brand.name LIKE %?1%" +
            "OR p.category.name LIKE %?1%")
    Page<Product> findAll(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 "
            + "OR p.category.allParentIDs LIKE %?2%")
    Page<Product> findAllInCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

    // For Category Search + keyword Search in the category Search
    // So first of all we will filter out the products by Category and then use the keyword
    @Query("SELECT p FROM Product p WHERE (p.category.id = ?1 "
            + "OR p.category.allParentIDs LIKE %?2%) AND "
           + "(p.name LIKE %?3% "
           + "OR p.id LIKE %?3% "
           + "OR p.shortDescription LIKE %?3% "
           + "OR p.fullDescription LIKE %?3% "
           + "OR p.brand.name LIKE %?3% "
           + "OR p.category.name LIKE %?3%)")
    Page<Product> searchInCategory(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable);

    @Query("Update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0),"
            + " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) "
            + "WHERE p.id = ?1")
    @Modifying    // COALESCE is used to return first non-null value
    void updateReviewCountAndAverageRating(Integer productId);

}
