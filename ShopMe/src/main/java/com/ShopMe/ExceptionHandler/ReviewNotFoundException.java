package com.ShopMe.ExceptionHandler;

public class ReviewNotFoundException extends RuntimeException{
    public ReviewNotFoundException() {
    }

    public ReviewNotFoundException(String message) {
        super(message);
    }
}
