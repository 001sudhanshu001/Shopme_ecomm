package com.ShopMe.Payloads;

import com.ShopMe.Entity.Role;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    private Integer id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String photos;
    private boolean enabled;

    private Set<Role> roles = new HashSet<>();
}
