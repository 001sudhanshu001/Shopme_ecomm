package com.ShopMe;

import com.ShopMe.DAO.BrandRepo;
import com.ShopMe.Entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class BrandRepoTest {

    @Autowired
    private BrandRepo brandRepo;

    public void testCreateBrand() {

    }
}
