package com.ShopMe.Controller;

import com.ShopMe.Entity.Brand;
import com.ShopMe.Entity.Product;
import com.ShopMe.ExceptionHandler.ProductNotFoundException;
import com.ShopMe.Service.Impl.BrandService;
import com.ShopMe.Service.Impl.ProductService;
import com.ShopMe.UtilityClasses.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.image.RescaleOp;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final BrandService brandService;

    @GetMapping("/products")
    public String listAll(Model model){
        List<Product> listProducts = productService.listAll();

        model.addAttribute("listProducts",listProducts);
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

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam("fileImage")MultipartFile mainImageMultipart,
                              @RequestParam("extraImage")MultipartFile[] extraImageMultiparts)
                                    throws IOException { // fileImage as we used in product_images.html

        setMainImage(mainImageMultipart, product);
        setExtraImagenames(extraImageMultiparts, product);

        Product savedProduct = productService.save(product);

        saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        productService.save(product);
        redirectAttributes.addFlashAttribute("message","The Product has been saved successfully");

        return "redirect:/products";
    }

    private void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts,
                                    Product savedProduct) throws IOException {
        if(!mainImageMultipart.isEmpty()) { // to save main image
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            String uploadDir = "../product-images/" + savedProduct.getId();

            FileUploadUtil.cleanDir(uploadDir); // clearing upload dir before uploading
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        if(extraImageMultiparts.length > 0) { // to save extra image
            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";

            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (!multipartFile.isEmpty()) {
                    continue;
                }
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }

    private void setMainImage(MultipartFile mainImageMultipart, Product product) {
        if(!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }
    private void setExtraImagenames(MultipartFile[] extraImageMultiparts, Product product){
        if(extraImageMultiparts.length > 0){
            for (MultipartFile multipartFile : extraImageMultiparts){
                if(!multipartFile.isEmpty()){
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    product.addExtraImage(fileName);
                }
            }
        }
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(id, enabled);
        String updatedStatus = enabled ? "enabled" : "disabled";
        String message = "The Product ID "+ id + " has been " + updatedStatus;

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes){
        try {
            productService.delete(id);
            String productMainImagesDir = "../product-images/" + id;
            String productExtraImagesDir = "../product-images/" + id + "/extras";

            FileUploadUtil.removeDir(productMainImagesDir);
            FileUploadUtil.removeDir(productExtraImagesDir);


            redirectAttributes.addFlashAttribute("message",
                    "The Product ID " + id + " has been deleted successfully");
        }catch (ProductNotFoundException ex){
            redirectAttributes.addAttribute("message", ex.getMessage());
        }

        return "redirect:/products";
    }
}
