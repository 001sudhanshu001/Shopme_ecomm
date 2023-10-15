package com.ShopMe.DAO;

import com.ShopMe.Entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepo extends JpaRepository<Country, Integer> {
    List<Country> findAllByOrderByNameAsc();
}
