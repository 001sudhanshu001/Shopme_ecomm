package com.ShopMe.Controller.RestCotnrollers;

import com.ShopMe.DAO.StateRepo;
import com.ShopMe.Entity.Country;
import com.ShopMe.Entity.State;
import com.ShopMe.Payloads.StateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StateRestController {
    private final StateRepo stateRepo;

    @GetMapping("/states/list_by_country/{id}")
    public List<StateDto> listByCountry(@PathVariable("id") Integer countryId){
        List<State> listStates = stateRepo.findByCountryOrderByNameAsc(new Country(countryId));
        List<StateDto> result = new ArrayList<>();

        for(State state : listStates){
            result.add(new StateDto(state.getId(), state.getName()));
        }
        return result;
    }

    @PostMapping("/states/save")
    public String save(@RequestBody State state){
        System.out.println("State Save Rest Controller Called");
        State savedState = stateRepo.save(state);

        return String.valueOf(savedState.getId());
    }

    @PostMapping("/sates/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
        System.out.println("State Delete Rest Controller Called");
        stateRepo.deleteById(id);
    }

}
