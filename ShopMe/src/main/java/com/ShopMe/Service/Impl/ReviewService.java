package com.ShopMe.Service.Impl;

import com.ShopMe.DAO.ProductRepo;
import com.ShopMe.DAO.ReviewRepository;
import com.ShopMe.Entity.Review;
import com.ShopMe.ExceptionHandler.ReviewNotFoundException;
import com.ShopMe.Service.ReviewServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;


@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService implements ReviewServiceI {

    public static final int REVIEWS_PER_PAGE = 5;

    private final ReviewRepository reviewRepo;

    private final ProductRepo productRepo;

    public Page<Review> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

        if(keyword != null){
            return reviewRepo.findAll(keyword, pageable);
        }

        return reviewRepo.findAll(pageable);
    }

    @Override
    public Review get(Integer id) throws ReviewNotFoundException {
        try {
            return reviewRepo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new ReviewNotFoundException("Could not find any reviews with ID " + id);
        }
    }

    @Override
    public void save(Review reviewInForm) {
        Review reviewInDB = reviewRepo.findById(reviewInForm.getId()).get();
        reviewInDB.setHeadline(reviewInForm.getHeadline());
        reviewInDB.setComment(reviewInForm.getComment());

        reviewRepo.save(reviewInDB);
        productRepo.updateReviewCountAndAverageRating(reviewInDB.getProduct().getId());
    }

    @Override
    public void delete(Integer id) throws ReviewNotFoundException {
        if (!reviewRepo.existsById(id)) {
            throw new ReviewNotFoundException("Could not find any reviews with ID " + id);
        }

        reviewRepo.deleteById(id);
    }

}
