package com.ShopMe.Controller;

import com.ShopMe.Entity.Brand;
import com.ShopMe.Entity.Category;
import com.ShopMe.ExceptionHandler.BrandNotFoundException;
import com.ShopMe.Service.Impl.BrandService;
import com.ShopMe.Service.Impl.CategoryService;
import com.ShopMe.UtilityClasses.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;
    private final CategoryService categoryService;
    @GetMapping("/brands")
    public String listFirstPage(Model model){

        List<Brand> listBrands = brandService.listAll();
        model.addAttribute("listBrands", listBrands);

        return "brands/brands";
    }
    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNUm, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){

        Page<Brand> page = this.brandService.listByPage(pageNUm, sortField, sortDir, keyword);
        List<Brand> listBrand = page.getContent();


        long startCount = (long) (pageNUm - 1) * BrandService.BRANDS_PER_PAGE + 1;
        long endCount = startCount +  BrandService.BRANDS_PER_PAGE - 1;

        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

//        model.addAttribute("currentPage", pageNUm);
//        model.addAttribute("totalPages", page.getTotalPages());
//        model.addAttribute("startCount", startCount);
//        model.addAttribute("endCount", endCount);
//        model.addAttribute("totalItems", page.getTotalElements());
//        model.addAttribute("sortField", sortField);
//        model.addAttribute("sortDir", sortDir);
//        model.addAttribute("reverseSortDir", reverseSortDir);
//        model.addAttribute("keyword", keyword);
//        model.addAttribute("listBrands", listBrand);

        model.addAttribute("currentPage", pageNUm);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listBrands", listBrand);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);

        return "brands/brands";

    }

    @GetMapping("/brands/new")
    public String newBrand(Model model){
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("brand", new Brand());
        model.addAttribute("pageTitle","Create New Brand");

        return "brands/brand_form";
    }

    @PostMapping("/brands/save")
    public String savebrand(Brand brand, @RequestParam("fileImage")MultipartFile multipartFile, RedirectAttributes ra)
            throws IOException {

        if(!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);

            Brand savedBrand = brandService.save(brand);
         //   String uploadDir = "../brand-logos/";
            String uploadDir = "../brand-logos/" + savedBrand.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }else {
            brandService.save(brand);
        }

        ra.addFlashAttribute("message", "The brand has been saved successfully");
        return "redirect:/brands";
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try {
            Brand brand = brandService.get(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Brand(ID: "+ id + ")");

            return "brands/brand_form";
        }catch (BrandNotFoundException ex){
            ra.addFlashAttribute("error_message", ex.getMessage());
            return "redirect:/brands";
        }

    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes){

        try {
            brandService.delete(id);
            // if we want to delete brand logo also
            String brandDir = "../brand-logos/" + id;
            FileUploadUtil.removeDir(brandDir);
            redirectAttributes.addFlashAttribute("message","The Brand with Id " + id +  " has been deleted successfully");


        }catch (BrandNotFoundException e){ // message mai vo message ayega jo humne UserService class se bheja hai
            redirectAttributes.addFlashAttribute("error_message",e.getMessage());
        }
        return "redirect:/brands"; // redirect to the users page with proper message

    }
}
