package com.ShopMe.Controller;

import com.ShopMe.Entity.*;
import com.ShopMe.ExceptionHandler.ProductNotFoundException;
import com.ShopMe.Security.ShopmeUserDetails;
import com.ShopMe.Service.Impl.BrandService;
import com.ShopMe.Service.Impl.CategoryService;
import com.ShopMe.Service.Impl.ProductService;
import com.ShopMe.UtilityClasses.AmazonS3Util;
import com.ShopMe.UtilityClasses.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final BrandService brandService;

    private final CategoryService categoryService;

    @GetMapping("/products")
    public String listFirstPage(Model model){
        return listByPage(1,model, "name","asc",null, 0);// Initially CategoryID is 0
//        List<Product> listProducts = productService.listAll();
//
//        model.addAttribute("listProducts",listProducts);
//        return "products/products";
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNUm, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword, @Param("categoryId") Integer categoryId ){

        Page<Product> page = this.productService.listByPage(pageNUm, sortField, sortDir, keyword, categoryId);
        List<Product> listProducts = page.getContent();

        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        long startCount = (long) (pageNUm - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount +  ProductService.PRODUCTS_PER_PAGE - 1;

        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        if(categoryId != null) model.addAttribute("categoryId", categoryId); // For search using Category
        model.addAttribute("currentPage", pageNUm);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("listCategories", listCategories);

        return "products/products";

    }

    @GetMapping("/products/new")
    public String newProduct(Model model){
        List<Brand> listBrands = brandService.listAll();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("numberOfExistingExtraImages", 0);


        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam(value = "fileImage", required = false)MultipartFile mainImageMultipart,
                              @RequestParam(value = "extraImage", required = false)MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,// same name as in product_images.html
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal ShopmeUserDetails loggedUser
                              ) throws IOException, ProductNotFoundException { // fileImage as we used in product_images.html

        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
            if (loggedUser.hasRole("Salesperson")) {
                productService.saveProductPrice(product);
                redirectAttributes.addFlashAttribute("message",
                        "The Product has been saved successfully");

                return "redirect:/products";
            }
        }
        // If user has Admin or Editor then the fileImage i.e. main photo is required
        // this is handled in product_image.html and product_image_readonly.html
        setMainImage(mainImageMultipart, product);
        setExistingExtraImagesName(imageIDs, imageNames, product);
        setNewExtraImagenames(extraImageMultiparts, product);

        // while updating
//        Product clearedProduct = productService.get(product.getId());
//        product.getDetails().clear();//Removing old details which are present in the database

        setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.save(product);

        saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);
        deleteExtraImagesThatWereRemovedOnForm(product);

        redirectAttributes.addFlashAttribute("message",
                "The Product has been saved successfully");
        return "redirect:/products";
    }

    public static void deleteExtraImagesThatWereRemovedOnForm(Product product) {
//        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        String extraImageDir = "product-images/" + product.getId() + "/extras";
        List<String> listObjectKeys = AmazonS3Util.listFolder(extraImageDir);

        for (String objectKey : listObjectKeys) {
            int lastIndexOfSlash = objectKey.lastIndexOf("/");
            String fileName = objectKey.substring(lastIndexOfSlash + 1, objectKey.length());
            if(!product.containsImageName(fileName)) {
                AmazonS3Util.deleteFile(objectKey);
            }
        }

        /*
        Path dirPath = Paths.get(extraImageDir);
        try {
            Files.list(dirPath).forEach(file -> {
                String filename = file.toFile().getName();
                if (!product.containsImageName(filename)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        log.error("Could not delete extra image: " + filename);
                    }
                }
            });
        } catch (IOException ex) {
            log.error("Could not list directory: " + dirPath);
        }
         */
    }

    private void setExistingExtraImagesName(String[] imageIDs, String[] imageNames, Product product) {
        if(imageIDs == null || imageIDs.length == 0) return;

        Set<ProductImage> images = new HashSet<>();

        for(int count = 0; count < imageIDs.length; count++){
            int id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];

            images.add(new ProductImage(id, name, product));
        }
        product.setImages(images);
    }

    private void setProductDetails(String[] detailIDs, String[] detailNames,
                                   String[] detailValues, Product product)  throws ProductNotFoundException {
        if(detailNames == null || detailNames.length == 0) return;

        for(int count = 0; count < detailNames.length; count++){
            String name = detailNames[count];
            String value = detailValues[count];
            int id = Integer.parseInt(detailIDs[count]);

            if (id != 0) {
                product.addDetails(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetails(name, value);
            }
        }
    }

    private void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts,
                                    Product savedProduct) throws IOException {
        if(!mainImageMultipart.isEmpty()) { // to save main image
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
//            String uploadDir = "../product-images/" + savedProduct.getId();
//
//            FileUploadUtil.cleanDir(uploadDir); // clearing upload dir before uploading
//            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);

            String uploadDir = "product-images/" + savedProduct.getId();

            List<String> listObjectKeys = AmazonS3Util.listFolder(uploadDir + "/");

            for(String objectKey : listObjectKeys) {
                if(!objectKey.contains("/extras/")) {
                    AmazonS3Util.deleteFile(objectKey);
                }
            }
            AmazonS3Util.uploadFile(uploadDir, fileName, mainImageMultipart.getInputStream());
        }

        if(extraImageMultiparts.length > 0) {
//            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
            String uploadDir = "product-images/" + savedProduct.getId() + "/extras";

            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (multipartFile.isEmpty()) {
                    continue;
                }
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
                AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

            }
        }
    }

    private void setMainImage(MultipartFile mainImageMultipart, Product product) {
        if(!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils
                    .cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
            product.setMainImage(fileName);
        }
    }
    private void setNewExtraImagenames(MultipartFile[] extraImageMultiparts, Product product){
        for (MultipartFile multipartFile : extraImageMultiparts) {
            if (!multipartFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                if (!product.containsImageName(fileName)) {
                    product.addExtraImage(fileName);
                }
            }
        }
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {

        productService.updateProductEnabledStatus(id, enabled);
        String updatedStatus = enabled ? "Enabled" : "Disabled";
        String message = "The Product ID "+ id + " has been " + updatedStatus;

        if(updatedStatus.equals("Enabled")){
            redirectAttributes.addFlashAttribute("message", message);
        }else {
            redirectAttributes.addFlashAttribute("disable_message",message);
        }

        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, Model model,
                                RedirectAttributes redirectAttributes){
        try {
            productService.delete(id);
            // deleting images related to this product
//            String productExtraImagesDir = "../product-images/" + id + "/extras";
//            String productMainImagesDir = "../product-images/" + id;
//
//            FileUploadUtil.removeDir(productExtraImagesDir);
//            FileUploadUtil.removeDir(productMainImagesDir);

            String productExtraImagesDir = "product-images/" + id + "/extras";
            String productMainImagesDir = "product-images/" + id;


            redirectAttributes.addFlashAttribute("message",
                    "The Product ID " + id + " has been deleted successfully");
            return "redirect:/products";
        }catch (ProductNotFoundException ex){
            redirectAttributes.addFlashAttribute("error_message", ex.getMessage());
            return "redirect:/products";
        }

    }

    // To edit product
    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model,
                              RedirectAttributes redirectAttributes){
        try{
            Product product = productService.get(id);

            List<Brand> listBrands = brandService.listAll();

            Integer numberOfExistingExtraImages = product.getImages().size();
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit Product (ID:" + id + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "products/product_form";
        }catch (ProductNotFoundException ex){
            redirectAttributes.addFlashAttribute("error_message", ex.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/details/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model,
                                     RedirectAttributes redirectAttributes){
        try{
            Product product = productService.get(id);
            model.addAttribute("product", product);

            return "products/product_detail_modal"; // this is logical page for the modal
        }catch (ProductNotFoundException ex){
            redirectAttributes.addFlashAttribute("error_message", ex.getMessage());
            return "redirect:/products";
        }
    }
}
