package com.ShopMe.Controller.RestCotnrollers;


import com.ShopMe.Service.Impl.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerRestController {
    private final CustomerService service;

    @PostMapping("/customers/check_unique_email")
    public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
        if (service.isEmailUnique(id, email)) {
            return "OK";
        } else {
            return "Duplicated";
        }
    }
}
