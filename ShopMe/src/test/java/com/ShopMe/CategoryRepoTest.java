package com.ShopMe;

import com.ShopMe.DAO.CategoryRepo;
import com.ShopMe.Entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepoTest {
    @Autowired
    private CategoryRepo categoryRepo;

    @Test
    public void testCreateRootCategory(){

    }

    @Test
    public void testFindByName() {
       String name = "Lenses";
       Category category = this.categoryRepo.findByName(name);
        System.out.println(category);

       System.out.println(category);
       assertThat(category).isNotNull();
       assertThat(category.getName()).isEqualTo(name);

    }

    @Test
    public void testFindByAlias() {
        String name = "lenses";
        Category category = this.categoryRepo.findByAlias(name);
        System.out.println(category);

        System.out.println(category);
        assertThat(category).isNotNull();
        assertThat(category.getAlias()).isEqualTo(name);

    }




}
