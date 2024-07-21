package com.ShopMe.Security;

import com.ShopMe.DAO.UserRepository;
import com.ShopMe.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

// this class will be called by Spring security during authentication process

public class ShopmeUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(email);
        if(user != null){
//            System.out.println(user.getRoles().getClass());
//            System.out.println("User Role Fetching..!");

            return new ShopmeUserDetails(user);
        }
        throw new UsernameNotFoundException("Could not find user with email" +email);
    }
}
