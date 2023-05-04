package com.ShopMe.DAO;

import com.ShopMe.Entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category, Integer> {

     @Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
     List<Category> findRootCategories(Sort sort);

    @Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
    Page<Category> findRootCategories(Pageable pageable);

     Long countById(Integer id);


   //  @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
     @Query("SELECT c FROM Category c WHERE CONCAT(c.id, ' ', c.name, ' ', c.alias) LIKE %?1%")
     Page<Category> search(String keyword, Pageable pageable);

     Category findByName(String name);
     Category findByAlias(String alias);

    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);
}
