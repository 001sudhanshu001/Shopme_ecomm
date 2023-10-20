package com.ShopMe.Service.Impl;

import com.ShopMe.DAO.BrandRepo;
import com.ShopMe.Entity.Brand;
import com.ShopMe.ExceptionHandler.BrandNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepo brandRepo;

    public static final int BRANDS_PER_PAGE = 5;

    public List<Brand> listAll() {
        return brandRepo.findAll();
    }

    public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword){
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);

        if(keyword != null){
            return brandRepo.findAll(keyword, pageable);
        }
        return brandRepo.findAll(pageable);
    }

    public Brand save(Brand brand){
        return brandRepo.save(brand);
    }

    public Brand get(Integer id) throws BrandNotFoundException {
        try {
            return brandRepo.findById(id).get();
        }catch (NoSuchElementException ex){
            throw new BrandNotFoundException("Could not find any brand with ID "+ id);
        }
    }

    public void delete(Integer id) throws BrandNotFoundException{
        Long countById = brandRepo.countById(id);

        if(countById == null || countById == 0){
            throw new BrandNotFoundException("Could not find any brand with ID "+ id);
        }
        brandRepo.deleteById(id);
    }

    public String checkUnique(Integer id, String name){
        boolean isCreatingNew = (id == null || id ==0);

        Brand brandByName = brandRepo.findByName(name);

        if(isCreatingNew){
            if (brandByName != null) return "Duplicated";
        }else { // while updating if user try to enter duplicate name
            if(brandByName != null && brandByName.getId() != id){
                return "Duplicated";
            }
        }
        return "OK";
    }
}
