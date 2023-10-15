package com.ShopMe.Controller.RestCotnrollers;


import com.ShopMe.Service.Impl.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerRestController {
    // This class is used to check uniqueness of email when Admin changes email of customer

    private final CustomerService service;

    @PostMapping("/customers/check_unique_email")
    public String checkDuplicateEmail(@Param("email") String email) {
        return service.isEmailUnique(email) ? "OK" : "Duplicated";
    }
}
