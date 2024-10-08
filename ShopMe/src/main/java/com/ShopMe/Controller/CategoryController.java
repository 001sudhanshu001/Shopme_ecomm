package com.ShopMe.Controller;

import com.ShopMe.Entity.Category;
import com.ShopMe.Entity.CategoryPageInfo;
import com.ShopMe.ExceptionHandler.CategoryNotFoundException;
import com.ShopMe.Exporter.Category.CategoryCsvExporter;
import com.ShopMe.Service.Impl.CategoryService;
import com.ShopMe.UtilityClasses.AmazonS3Util;
import com.ShopMe.UtilityClasses.FileUploadUtil;
import io.github.classgraph.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final AmazonS3Util amazonS3Util;

    @GetMapping({"/categories", "/categories/page"})
    private String listFirstPage(Model model){
        return listByPage(1,"name", "asc", null, model);

    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable("pageNum") int pageNum,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword,
                             Model model){

        if(sortDir == null || sortDir.isEmpty()){
            sortDir  = "asc";
        }

        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> categories = categoryService
                .listByPage(pageInfo, pageNum,sortField, sortDir, keyword);

        addPreResignedURL(categories);

        long startCount = (long) (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;

        if(endCount > pageInfo.getTotalElements()){
            endCount = pageInfo.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalPages",pageInfo.getTotalPage());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("categories",categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        return "categories/categories";
    }

    private void addPreResignedURL(List<Category> categoryPage) {
        for(Category category : categoryPage) {
            if(category.getImage() != null && !category.getImage().isEmpty()) {
                String resignedUrl =
                        amazonS3Util.generatePreSignedUrl("category-images/"
                                + category.getId() + "/" + category.getImage());
                category.setPreSignedURL(resignedUrl);
            }
        }
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model){
        List<Category> listCategories = this.categoryService.listCategoriesUsedInForm(); // to show in dropdown

        Category category = new Category();
        category.setPreSignedURL("/images/image-thumbnail.png"); // To show dummy Image

        model.addAttribute("category", category);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create new category" );

        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category,
                               @RequestParam("fileName")MultipartFile multipartFile,
                               RedirectAttributes attributes) throws IOException {

        // TODO : Give error and also check for Non Null
        if(category.getName().length() > 127) {
            category.setName(category.getName().substring(0, 126));
        }

        if(category.getAlias().length() > 63) {
            category.setAlias(category.getAlias().substring(0, 63));
        }

        // Generate unique name for image using Timestamp
        if(category.getImage().length() > 128) {
            category.setImage(category.getImage().substring(0, 63));
        }

        if(category.getAlias().contains("/")) {
            category.setAlias(category.getAlias().replace("/", "-"));
        }

        if(!multipartFile.isEmpty()){
            String fileName = StringUtils
                    .cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            category.setImage(fileName);

            Category savedCategory = this.categoryService.save(category);

//            String uploadDir = "../category-images/" + savedCategory.getId();
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            String uploadDir = "category-images/" + savedCategory.getId();
            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
        }else {
            this.categoryService.save(category);
        }

        attributes.addFlashAttribute("message",
                "The category has been saved successfully ");
        return "redirect:/categories";

    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable("id") Integer id, Model model,
                               RedirectAttributes attributes){
        try {
            Category category = this.categoryService.get(id);
            List<Category> listCategories = this.categoryService.listCategoriesUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");

            return "categories/category_form";
        }catch (CategoryNotFoundException ex){
            attributes.addFlashAttribute("error_message", ex.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled,
                                              RedirectAttributes attributes){

        this.categoryService.updateCategoryEnabledStatus(id, enabled);
        String stat = enabled ? "enabled" : "disabled";
        String message = "This category ID  " + id + " has been " + stat;

        attributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id,
                                 Model model,
                                 RedirectAttributes attributes){
        try{
            categoryService.delete(id);
//            String categoryDir = "../category-images/" + id;
//            FileUploadUtil.removeDir(categoryDir);

            String categoryDir = "category-images/" + id;
            AmazonS3Util.removeFolder(categoryDir);

            attributes.addFlashAttribute("message",
                    "The category having ID " +id + " has been deleted successfully");
        }catch (CategoryNotFoundException ex){
            attributes.addFlashAttribute("error_message", ex.getMessage());
        }

        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException{
        List<Category> listCategory = categoryService.listCategoriesUsedInForm();
        CategoryCsvExporter exporter = new CategoryCsvExporter();
        exporter.export(listCategory, response );
    }

}
