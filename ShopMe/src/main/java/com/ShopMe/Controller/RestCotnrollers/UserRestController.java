package com.ShopMe.Controller.RestCotnrollers;

import com.ShopMe.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email){
        return this.userService.isEmailUnique(id, email)  ? "OK" : "Duplicated";
    }

}
