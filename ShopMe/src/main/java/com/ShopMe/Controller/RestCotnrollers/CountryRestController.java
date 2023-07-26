package com.ShopMe.Controller.RestCotnrollers;

import com.ShopMe.DAO.CountryRepo;
import com.ShopMe.Entity.Country;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CountryRestController {
    private final CountryRepo repo;

    @GetMapping("/countries/list")
    public List<Country> listAll() {
        return repo.findAllByOrderByNameAsc();
    }

    @PostMapping("/countries/save")
    public String save(@RequestBody Country country){
        Country savedCountry = repo.save(country);
        return String.valueOf(savedCountry.getId());
    }

    @GetMapping("/countries/delete/{id}")
    public void delete(@PathVariable("id") Integer id){
        repo.deleteById(id);
    }
}
