package com.ShopMe.Service.Impl;

import com.ShopMe.DAO.ProductRepo;
import com.ShopMe.Entity.Product;
import com.ShopMe.ExceptionHandler.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepo repo;

    public List<Product> listAll() {
        return repo.findAll();
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

        return repo.save(product);
    }

    public String checkUnique(Integer id, String name){
        boolean isCreatingNew = (id==null || id == 0);

        Product productByName = repo.findByName(name);

        if(isCreatingNew){ // new Product is creating and this Product Name already Exists
            if(productByName != null) return "Duplicate";
        }else { // while updating
            if(productByName != null && productByName.getId() != id){
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


}
