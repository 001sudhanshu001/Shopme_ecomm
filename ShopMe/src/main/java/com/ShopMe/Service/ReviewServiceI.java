package com.ShopMe.Service;

import com.ShopMe.Entity.Review;
import com.ShopMe.ExceptionHandler.ReviewNotFoundException;

public interface ReviewServiceI {
    Review get(Integer id) throws ReviewNotFoundException;
    void save(Review reviewInForm);
    void delete(Integer id) throws ReviewNotFoundException;

}
