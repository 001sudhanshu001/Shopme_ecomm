package com.ShopMe.DAO;

import com.ShopMe.Entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepo extends JpaRepository<Currency, Integer> {
    List<Currency> findAllByOrderByNameAsc();
}
