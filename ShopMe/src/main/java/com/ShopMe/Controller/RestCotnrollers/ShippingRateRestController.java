package com.ShopMe.Controller.RestCotnrollers;

import com.ShopMe.ExceptionHandler.ShippingRateNotFoundException;
import com.ShopMe.Service.Impl.ShippingRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ShippingRateRestController {

    private final ShippingRateService service;

    @PostMapping("/get_shipping_cost")
    public String getShippingCost(Integer productId, Integer countryId, String state)
            throws ShippingRateNotFoundException {
        float shippingCost = service.calculateShippingCost(productId, countryId, state);
        return String.valueOf(shippingCost);
    }
}