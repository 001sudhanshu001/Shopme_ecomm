package com.ShopMe.Controller.RestCotnrollers;

import com.ShopMe.Service.Impl.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryService categoryService;

    @PostMapping("/categories/check_unique") // used in category form javascript
    public String checkUnique(@RequestParam("id") Integer id, @RequestParam("name") String name,
                              @RequestParam("alias") String alias) {

        return categoryService.checkUniqueCategory(id, name, alias);

    }
}
