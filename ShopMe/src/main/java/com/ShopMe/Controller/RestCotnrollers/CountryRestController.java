package com.ShopMe.Controller.RestCotnrollers;

import com.ShopMe.DAO.CountryRepo;
import com.ShopMe.Entity.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CountryRestController { // This Controller handles AJAX call from Settings -> countries
    private final CountryRepo repo;

    @GetMapping("/countries/list")
    public List<Country> listAll() {
        List<Country> countryList = repo.findAllByOrderByNameAsc();

        return countryList;
    }

    @PostMapping("/countries/save")
    public String save(@RequestBody Country country){
        Country savedCountry = repo.save(country);
        return String.valueOf(savedCountry.getId());
    }

    @DeleteMapping("/countries/delete/{id}")
    public void delete(@PathVariable("id") Integer id){
        repo.deleteById(id);
    }
}
