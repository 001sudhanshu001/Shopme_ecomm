package com.ShopMe.DAO;

import com.ShopMe.Entity.Country;
import com.ShopMe.Entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepo extends JpaRepository<State, Integer> {
    List<State> findByCountryOrderByNameAsc(Country country);
}
