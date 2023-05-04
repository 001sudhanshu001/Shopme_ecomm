package com.ShopMe.DAO;

import com.ShopMe.Entity.User;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<User, Integer> {
//    @Query("SELECT u FROM User u WHERE u.email= :email")
//    User findByEmail(@Param("email") String email);

      User findByEmail(String email);

      Long countById(Integer id); // to check whether the user is present on not

      @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
      @Modifying
      void updateEnabledStatus(Integer id, boolean enabled);

//      @Query("SELECT u FROM User u WHERE u.firstName LIKE %?1% OR u.lastName LIKE %?1% OR u.email LIKE %?1%")
//      Page<User> findAll(String keyword, Pageable pageable);

      @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
      Page<User> findAll(String keyword, Pageable pageable);

}
