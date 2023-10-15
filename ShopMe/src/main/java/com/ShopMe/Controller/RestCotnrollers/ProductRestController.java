package com.ShopMe.Controller.RestCotnrollers;

import com.ShopMe.Service.Impl.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductRestController {
    private final ProductService productService;

    @PostMapping("/products/check_unique")
    public String checkUnique(@Param("id") Integer id, @Param("name") String name) {
        System.out.println("Inside checkUnique");
        return productService.checkUnique(id, name);
    }
}
