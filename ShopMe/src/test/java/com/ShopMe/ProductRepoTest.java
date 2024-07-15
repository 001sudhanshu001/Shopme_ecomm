package com.ShopMe;

import com.ShopMe.DAO.ProductRepo;
import com.ShopMe.Entity.Brand;
import com.ShopMe.Entity.Category;
import com.ShopMe.Entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepoTest {

    @Autowired
    private ProductRepo repo;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void  testCreateProduct(){
        Brand brand = entityManager.find(Brand.class, 8);
        Category category = entityManager.find(Category.class, 4);

        Product product = new Product();
        product.setName("Xiomi mobile");
        product.setAlias("Xiomi mobile");
        product.setShortDescription("A good phone from Xiomi");
        product.setFullDescription("This is gaming expert");

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(320);
        product.setCost(70);
        product.setEnabled(true);
        product.setInStock(true);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = repo.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProducts(){
        Iterable<Product> productIterable = repo.findAll();

        productIterable.forEach(System.out::println);
    }

    @Test
    public void testGetProduct() {
        Integer id = 2;
        Product product = repo.findById(id).get();
        System.out.println(product);
        assertThat(product).isNotNull();
    }

    @Test
    public void testUpdate(){
        Integer id = 1;
        Product product = repo.findById(id).get();

        product.setPrice(499);
        repo.save(product);

        Product updatedProduct = entityManager.find(Product.class, id);

        assertThat(updatedProduct.getPrice()).isEqualTo(499);
    }

    @Test
    public void testDeleteProduct() {
//        Integer id = 3;
//
//        repo.deleteById(id);
//
//        Optional<Product> result = repo.findById(3);
//
//        assertThat(!result.isPresent());
    }

    @Test
    public void testSaveProductWithImages(){
        Integer productId = 1;
        Product product = repo.findById(productId).get();

        product.setMainImage("mainImage4.jpg");
        product.addExtraImage("EI10.png");
        product.addExtraImage("EI211.png");
        product.addExtraImage("EI311.png");

        Product savedProduct = repo.save(product);

       assertThat(savedProduct.getImages().size()).isEqualTo(3);
    }

    @Test
    public void testSaveProductWithDetails() {
        Integer productId = 1;
        Product product = repo.findById(productId).get();

        product.addDetails("Device Memory", "128GB");
        product.addDetails("CPU Model", "MediaTek");
        product.addDetails("OS","Android 10");

        Product savedProduct = repo.save(product);
        assertThat(savedProduct.getDetails()).isNotNull();
    }

    @Test
    public void testUpdateReviewCountAndAverageRating() {
        Integer productId = 1;
        repo.updateReviewCountAndAverageRating(productId);
    }

}
