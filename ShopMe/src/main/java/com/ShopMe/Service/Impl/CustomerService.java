package com.ShopMe.Service.Impl;

import com.ShopMe.DAO.CountryRepo;
import com.ShopMe.DAO.CustomerRepo;
import com.ShopMe.Entity.Country;
import com.ShopMe.Entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    // This is used to fetch all the countries from DB to show into Customer Registration Page
    private final CountryRepo countryRepo;
    private final CustomerRepo customerRepo;

    public List<Country> listAllCountries(){
        return countryRepo.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email){
        Customer customer = customerRepo.findByEmail(email);

        return customer == null;
    }
}
