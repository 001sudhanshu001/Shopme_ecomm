package com.ShopMe.ExceptionHandler;

public class ShippingRateNotFoundException extends Exception{
    public ShippingRateNotFoundException(String message) {
        super(message);
    }
}
