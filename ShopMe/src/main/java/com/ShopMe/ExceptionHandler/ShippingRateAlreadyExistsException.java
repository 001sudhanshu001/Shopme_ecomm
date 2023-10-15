package com.ShopMe.ExceptionHandler;

public class ShippingRateAlreadyExistsException extends Exception{
    public ShippingRateAlreadyExistsException(String message) {
        super(message);
    }
}
