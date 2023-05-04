package com.ShopMe.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {
        return new ShopmeUserDetailsService();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
         http
                 .authorizeRequests()
                 .antMatchers("/users/**").hasAuthority("Admin")
                 .antMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
                 .antMatchers("/products").hasAnyAuthority("Admin", "Editor", "SalesPerson", "Shipper")
                 .anyRequest().authenticated()
                 .and()
                     .formLogin()
                     .usernameParameter("email")
                     .loginPage("/login").permitAll()
                 .and().logout().permitAll()
                 .and()
                        .rememberMe()
                                .key("Abcdefghijkllmnopqrs_123456789")
                                .tokenValiditySeconds(7 * 24 * 60 * 60); // 2 weeks


    }

    @Override
    public void configure(WebSecurity web) throws Exception { // for ant matching
        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
    }

}