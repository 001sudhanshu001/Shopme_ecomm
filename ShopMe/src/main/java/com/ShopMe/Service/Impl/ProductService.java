package com.ShopMe.Service.Impl;

import com.ShopMe.DAO.ProductRepo;
import com.ShopMe.Entity.Product;
import com.ShopMe.ExceptionHandler.ProductNotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepo repo;

    public static final int PRODUCTS_PER_PAGE = 3;

    private final AmazonS3 amazonS3;

    public List<Product> listAll() {
        return repo.findAll();
    }

    public Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyword, Integer categoryId){
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);

        if(keyword != null && !keyword.isEmpty()){
            if(categoryId != null && categoryId > 0){ // Search in filter by Category
                String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
                return repo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            }
            // if normal Search
            return repo.findAll(keyword, pageable);
        }
        if(categoryId != null && categoryId > 0){
            String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
            return repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
        }
        return repo.findAll(pageable);
    }

    public Product save(Product product){
        if(product.getId() == null){
            product.setCreatedTime(new Date());
        }

        if(product.getAlias() == null || product.getAlias().isEmpty()){
            String defaultAlias = product.getName().replaceAll(" ","-");
            product.setAlias(defaultAlias);
        }else {
            product.setAlias(product.getAlias().replaceAll(" ","-"));
        }

        product.setUpdatedTime(new Date());

        Product savedProduct = repo.save(product);
        repo.updateReviewCountAndAverageRating(savedProduct.getId());

        return savedProduct;
    }

    public void saveProductPrice(Product productInForm) { // This is used in the case when Salesperson is allowed to change only pricing
        // TODO ->
        Product productInDB = repo.findById(productInForm.getId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());

        repo.save(productInDB);
    }

    public String checkUnique(Integer id, String name){
        boolean isCreatingNew = (id==null || id == 0);

        Product productByName = repo.findByName(name);

        if(isCreatingNew){
            if(productByName != null) return "Duplicate";
        }else { // while updating
            if(productByName != null && !Objects.equals(productByName.getId(), id)){
                return "Duplicate";
            }
        }
        return "OK";
    }

    public void updateProductEnabledStatus(Integer id, boolean enabled){
        repo.updateEnabledStatus(id, enabled);
    }

    public void delete(Integer id) throws ProductNotFoundException {
        Long countById = repo.countById(id);

        if(countById == null || countById == 0){
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }

        repo.deleteById(id);
    }

    public Product get(Integer id) throws ProductNotFoundException {
        try {
            return repo.findById(id).get();
        }catch (NoSuchElementException ex){
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
    }

}
