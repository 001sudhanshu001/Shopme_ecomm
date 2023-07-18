package com.ShopMe.Service.Impl;

import com.ShopMe.DAO.CategoryRepo;
import com.ShopMe.Entity.Category;
import com.ShopMe.Entity.CategoryPageInfo;
import com.ShopMe.ExceptionHandler.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional // to update enabled category status
public class CategoryService {

    public static final int ROOT_CATEGORIES_PER_PAGE = 1;
    private final CategoryRepo categoryRepo;

//    @PostConstruct
//    void init() {
//        System.out.println("CategoryService Created..!");
//        System.out.println(categoryRepo.getClass());
//    }

    public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortField, String sortDir,
                                     String keyword) {

        Sort sort = Sort.by(sortField);

        if(sortDir.equals("asc")){
            sort = sort.ascending();
        }else if(sortDir.equals("desc")){
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(pageNum-1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories = null;
        if(keyword != null && !keyword.isEmpty()){ // means it is form searching
             pageCategories = categoryRepo.search(keyword, pageable);
        }else {
             pageCategories =  categoryRepo.findRootCategories(pageable);
        }

        List<Category> rootCategories = pageCategories.getContent();
        //***************************************
        System.out.println("In Service" + rootCategories);

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPage(pageCategories.getTotalPages());


        //********************************************
        System.out.println("In Category Service Total pages : " + pageCategories.getTotalPages());
        System.out.println("*********** Page category total elements" + pageCategories.getTotalElements());
        if(keyword != null && !keyword.isEmpty()){
            List<Category> searchResult = pageCategories.getContent();
            for(Category category : searchResult){
                category.setHasChildren(category.getChildren().size() > 0);
            }
            return searchResult;
        }else {
            return listHierarchicalCategories(rootCategories, sortDir);
        }

    }
    private  List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir){
        List<Category> hierarchicalCategories = new ArrayList<>();

        for(Category rootCategory : rootCategories){
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

            for(Category subCategory : children){
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent,
                                               int subLevel, String sortDir) {

        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);

        int newSubLevel = subLevel + 1;

        for(Category subCategory : children){
            StringBuilder name = new StringBuilder();
            for(int i = 0; i < newSubLevel; i++){
                name.append("--");
            }

            name.append(subCategory.getName());

            hierarchicalCategories.add(Category.copyFull(subCategory, name.toString()));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
        }

    }

    public Category save(Category category){
        // To store the id of all parent's
        Category parent = category.getParent();
        if(parent != null){ // if this is not null then means it is not root category
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += String.valueOf(parent.getId()) + "-"; // contacting with the Id of Direct Parent Id
            category.setAllParentIDs(allParentIds);
        }// otherwise we can save directly
        return this.categoryRepo.save(category);
    }

    public List<Category> listCategoriesUsedInForm(){
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = this.categoryRepo.findRootCategories(Sort.by("name").ascending());

        for(Category category : categoriesInDB){
            if(category.getParent() == null){
                categoriesUsedInForm.add(Category.copyIdAndName(category));

                Set<Category> children = sortSubCategories(category.getChildren());

                for(Category subCategory : children){
                    String name = "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
                    listSubCatogoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
                }
            }
        }

        return categoriesUsedInForm;
    }

    private void listSubCatogoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;

        Set<Category> children = sortSubCategories(parent.getChildren());

        for(Category subCategory : children){
            String name = "";
            for(int i = 0; i < newSubLevel; i++){
                name += "--";
            }

            name += subCategory.getName();

            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

            listSubCatogoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }

    public Category get(Integer id) throws CategoryNotFoundException {
        try {
            return this.categoryRepo.findById(id).get();
        }catch (NoSuchElementException ex){
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
    }


    public String checkUniqueCategory(Integer id, String name, String alias){
        boolean isCreatingNew = (id == null || id ==0);

        Category categoryByName = this.categoryRepo.findByName(name);

        if(isCreatingNew){ /// if creating new category
            if(categoryByName != null){
                return "DuplicateName";
            }else {
                Category categoryByAlias = categoryRepo.findByAlias(alias);
                if(categoryByAlias != null){
                    return "DuplicateAlias"   ;
                }
            }
        }else { // if editing the category
            if(categoryByName != null && categoryByName.getId() != id){
                return "DuplicateName";
            }

            Category categoryByAlias = categoryRepo.findByAlias(alias);
            if(categoryByAlias != null && categoryByAlias.getId() != id){
                return "DuplicateAlias";
            }
        }
        return "OK";
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return  sortSubCategories(children, "asc");
    }
    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if(sortDir.equals("asc")){
                    return cat1.getName().compareTo(cat2.getName());
                }else {
                    return cat2.getName().compareTo(cat1.getName());
                }

            }
        });
        sortedChildren.addAll(children);

        return sortedChildren;
    }

    public void updateCategoryEnabledStatus(Integer id, boolean enabled){
        this.categoryRepo.updateEnabledStatus(id, enabled);
    }

    public void delete(Integer id) throws CategoryNotFoundException {
        Long countById = this.categoryRepo.countById(id);

        if(countById == null || countById == 0){
            throw new CategoryNotFoundException("Could not found any category with ID " +id);
        }

        this.categoryRepo.deleteById(id);
    }


}
