package com.ShopMe.Controller.RestCotnrollers;

import com.ShopMe.Entity.Brand;
import com.ShopMe.Entity.Category;
import com.ShopMe.ExceptionHandler.BrandNotFoundException;
import com.ShopMe.ExceptionHandler.BrandNotFoundRestException;
import com.ShopMe.Payloads.CategoryDto;
import com.ShopMe.Service.Impl.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class BrandRestController {
    private final BrandService brandService;

    @PostMapping("/brands/check_unique")
    public String checkUnique(@Param("id") Integer id, @Param("name") String name){
        return brandService.checkUnique(id, name);
    }

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDto> listCategoriesByBrand(@PathVariable("id") Integer brandId)
            throws BrandNotFoundRestException {

        List<CategoryDto> listCategories = new ArrayList<>();
        try {
            Brand brand = brandService.get(brandId);
            Set<Category> categories = brand.getCategories();

            for(Category category : categories){
                CategoryDto dto = new CategoryDto(category.getId(), category.getName());
                listCategories.add(dto);
            }

            return listCategories;
        } catch (BrandNotFoundException e) {

            throw new BrandNotFoundRestException();
        }
    }
}
