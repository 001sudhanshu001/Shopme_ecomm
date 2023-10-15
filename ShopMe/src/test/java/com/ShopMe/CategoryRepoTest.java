package com.ShopMe;

import com.ShopMe.DAO.CategoryRepo;
import com.ShopMe.Entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

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
    public void testPrintHierarchicalCategories() {
        List<Category> categories = categoryRepo.findAll();

        for(Category category : categories){
            if(category.getParent() == null){
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();

                for(Category subCategory : children){
                    System.out.println("--" + subCategory.getName());
                    printChildrem(subCategory, 1);
                }
            }
        }
    }

    private void printChildrem(Category parent, int subLevel){
        int newSubLevel = subLevel +  1;
        Set<Category> children = parent.getChildren();

        for(Category subCategory : children){
            for(int i = 0; i < newSubLevel; i++){
                System.out.print("--");
            }
            System.out.println("--" + subCategory.getName());

            printChildrem(subCategory, newSubLevel);
        }
    }

    @Test
    public void testListRootCategoreis(){
      //  categoryRepo.findRootCategories();
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
