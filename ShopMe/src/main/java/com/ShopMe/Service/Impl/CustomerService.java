package com.ShopMe.Service.Impl;

import com.ShopMe.DAO.CountryRepo;
import com.ShopMe.DAO.CustomerRepo;
import com.ShopMe.Entity.Country;
import com.ShopMe.Entity.Customer;
import com.ShopMe.ExceptionHandler.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    // In FrontEnd CustomerService there are methods for creating the Customer from Registration page
    // In this CustomerService we will have methods for listing the customer for Admin

    public static final int CUSTOMERS_PER_PAGE = 10;
    private final CustomerRepo customerRepo;
    private final CountryRepo countryRepo;
    private PasswordEncoder passwordEncoder;

    public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1,CUSTOMERS_PER_PAGE, sort);

        if (keyword != null){
            return customerRepo.findAll(keyword, pageable);
        }

        return customerRepo.findAll(pageable);
    }

    public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
        customerRepo.updateEnabledStatus(id, enabled);
    }

    public Customer get(Integer id) throws CustomerNotFoundException {
        return customerRepo.findById(id).orElseThrow(()-> new CustomerNotFoundException("Could not find any customers with ID " + id));
    }

    public List<Country> listAllCountries() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email){
        Customer customer = customerRepo.findByEmail(email);

        return customer == null;
    }

    // This is the method used to save the customer if the Admin update the customer
    public void save(Customer customerInForm) {
        Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();

        if (!customerInForm.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
            customerInForm.setPassword(encodedPassword);
        } else {
            customerInForm.setPassword(customerInDB.getPassword());
        }
        // Since in the Editing form are not sending created time, enabled status, and verification code
        // so if we update it then all these three values will be null
        // so we have to add these also explicity
        customerInForm.setEnabled(customerInDB.isEnabled());
        customerInForm.setCreatedTime(customerInDB.getCreatedTime());
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());

        customerRepo.save(customerInForm);
    }

    public void delete(Integer id) throws CustomerNotFoundException {
        Long count = customerRepo.countById(id);
        if (count == null || count == 0) {
            throw new CustomerNotFoundException("Could not find any customers with ID " + id);
        }
        customerRepo.deleteById(id);
    }


    public List<Customer> listAll() {

        Sort firstNameSorting =  Sort.by("id").ascending();

        List<Customer> customerList = new ArrayList<>();
        customerList.addAll(customerRepo.findAll(firstNameSorting));
        return customerList;
    }

}
