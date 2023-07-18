package com.ShopMe.Security;

import com.ShopMe.Entity.Role;
import com.ShopMe.Entity.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ShopmeUserDetails implements UserDetails {

    private User user;
    private static final long serialVersionUID = 1L;

    public ShopmeUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("getAuthorities called");
        Set<Role> roles = this.user.getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for(Role role : roles){
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
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

    public boolean hasRole(String roleName){
        return this.user.hasRole(roleName);
    }
}
