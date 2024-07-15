package com.ShopMe.review;

import com.ShopMe.DAO.ReviewRepository;
import com.ShopMe.Entity.Customer;
import com.ShopMe.Entity.Product;
import com.ShopMe.Entity.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository repo;

    @Test
    public void testCreateReview() {

        Integer productId = 1;
        Product product = new Product(productId);

        Integer customerId = 13;
        Customer customer = new Customer(customerId);

        Review review = new Review();
        review.setHeadline("Nice! But can be better");
        review.setComment("It lacks some necessary featues");
        review.setReviewTime(new Date());
        review.setRating(3);
        review.setCustomer(customer);
        review.setProduct(product);

        Review savedReview = repo.save(review);

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getId()).isGreaterThan(0);
    }

    @Test
    public void testListReviews() {
        List<Review> listReviews = repo.findAll();
        listReviews.forEach(System.out::println);

        assertThat(listReviews.size()).isGreaterThan(0);
    }

    @Test
    public void testGetReviewById() {
        Integer id = 1;
        Review review = repo.findById(id).get();

        assertThat(review).isNotNull();

        System.out.println(review);
    }

    @Test
    public void testUpdateReviewById() {
        Integer id = 4;
        Review review = repo.findById(id).get();

        String headline = "An awesome phone at an awesome price";
        String comment = "Overall great phone and is highly capable...";

        review.setHeadline(headline);
        review.setComment(comment);

        Review updatedReview = repo.save(review);

        assertThat(updatedReview.getHeadline()).isEqualTo(headline);
        assertThat(updatedReview.getComment()).isEqualTo(comment);
    }

    @Test
    public void testDeleteReviewById() {
        Integer id = 1;
        repo.deleteById(id);

        Optional<Review> findById = repo.findById(id);

        assertThat(findById).isNotPresent();
    }
}

