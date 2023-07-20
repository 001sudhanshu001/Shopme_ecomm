package com.ShopMe;

import com.ShopMe.DAO.CurrencyRepo;
import com.ShopMe.Entity.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CurrencyRepoTest {
    @Autowired
    private CurrencyRepo repo;

    @Test
    public void testCreateCurrencies() {
        List<Currency> listCurrencies = Arrays.asList(
                new Currency("Indian Rupee", "₹", "INR"),
                new Currency("United States Dollar", "$", "USD")
        );

        repo.saveAll(listCurrencies);
        List<Currency> all = repo.findAll();

        for(Currency currency : all){
            assertThat(currency).isNotNull();
        }
    }

    @Test
    public void testListAllOrderByNameAsc() {
        List<Currency> currencies = repo.findAllByOrderByNameAsc();

        currencies.forEach(currency -> System.out.println(currency));

        assertThat(currencies.size()).isGreaterThan(0);
    }
}
