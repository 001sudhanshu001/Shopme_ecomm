package com.ShopMe.Security;

import com.ShopMe.Entity.Role;
import com.ShopMe.Entity.User;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ShopmeUserDetails implements UserDetails {

    private User user;

    public ShopmeUserDetails(User user) {
        System.out.println(1);
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println(2);
        Set<Role> roles = this.user.getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for(Role role : roles){
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        System.out.println(3);
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        System.out.println(4);
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        System.out.println(5);
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        System.out.println(6);
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        System.out.println(7);
        return true;
    }

    @Override
    public boolean isEnabled() {
        System.out.println(8);
        return user.isEnabled();
    }

    public String getFullname() {
        return this.user.getFirstName() +  " " + this.user.getLastName();
    }

    public void setFirstName(String firstName){
        this.user.setFirstName(firstName);
    }
    public void setlastName(String lastName){
        this.user.setFirstName(lastName);
    }
}
