package com.ShopMe.DAO;

import com.ShopMe.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepo extends JpaRepository<Product, Integer> {

    Product findByName(String name);

    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    Long countById(Integer id); // while deleting we will check if any Product with this id exists or not
}
