package com.ShopMe.DAO;

import com.ShopMe.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

}
