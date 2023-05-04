package com.ShopMe.ExceptionHandler;

public class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
