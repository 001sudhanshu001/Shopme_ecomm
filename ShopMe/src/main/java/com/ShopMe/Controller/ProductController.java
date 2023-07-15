package com.ShopMe.Controller;

import com.ShopMe.Entity.Brand;
import com.ShopMe.Entity.Product;
import com.ShopMe.Entity.ProductImage;
import com.ShopMe.ExceptionHandler.ProductNotFoundException;
import com.ShopMe.Service.Impl.BrandService;
import com.ShopMe.Service.Impl.ProductService;
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

import java.awt.image.RescaleOp;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final BrandService brandService;

    @GetMapping("/products")
    public String listFirstPage(Model model){
        return listByPage(1,model, "name","asc",null);
//        List<Product> listProducts = productService.listAll();
//
//        model.addAttribute("listProducts",listProducts);
//        return "products/products";
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNUm, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){

        Page<Product> page = this.productService.listByPage(pageNUm, sortField, sortDir, keyword);
        List<Product> listProducts = page.getContent();


        long startCount = (long) (pageNUm - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount +  ProductService.PRODUCTS_PER_PAGE - 1;

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
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);

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
                              @RequestParam("extraImage")MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,// same name as in product_images.html
                              @RequestParam(name = "imageNames", required = false) String[] imageNames
                              )
                                    throws IOException { // fileImage as we used in product_images.html

        setMainImage(mainImageMultipart, product);
        setExistingExtraImagesName(imageIDs, imageNames, product);
        setNewExtraImagenames(extraImageMultiparts, product);
        setProductDetials(detailNames, detailValues, product);

        Product savedProduct = productService.save(product);

        saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        productService.save(product);
        redirectAttributes.addFlashAttribute("message","The Product has been saved successfully");

        return "redirect:/products";
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

    private void setProductDetials(String[] detailNames, String[] detailValues, Product product) {
        if(detailNames == null || detailNames.length == 0) return;

        for(int count = 0; count < detailNames.length; count++){
            String name = detailNames[count];
            String value = detailValues[count];

            if(!name.isEmpty() && !value.isEmpty()){
                product.addDetails(name, value);
            }
        }
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
            System.out.println("saving extra images");
            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";

            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (multipartFile.isEmpty()) {
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
    private void setNewExtraImagenames(MultipartFile[] extraImageMultiparts, Product product){
        if(extraImageMultiparts.length > 0){
            for (MultipartFile multipartFile : extraImageMultiparts){
                if(!multipartFile.isEmpty()){
                    System.out.println("Setting extra image");
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                    if(!product.containsImageName(fileName)){
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {

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
    public String deleteProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes){
        try {
            productService.delete(id);
            // deleting images related to this product
            String productExtraImagesDir = "../product-images/" + id + "/extras";
            String productMainImagesDir = "../product-images/" + id;

            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productMainImagesDir);

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
    public String editProduct(@PathVariable("id") Integer id, Model model,  RedirectAttributes redirectAttributes){
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
    public String viewProductDetails(@PathVariable("id") Integer id, Model model,  RedirectAttributes redirectAttributes){
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
