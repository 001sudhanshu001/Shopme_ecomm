package com.ShopMe.Service.Impl;

import com.ShopMe.DAO.RoleRepository;
import com.ShopMe.DAO.UserRepository;
import com.ShopMe.Entity.Role;
import com.ShopMe.Entity.User;
import com.ShopMe.ExceptionHandler.UserNotFoundException;
import com.ShopMe.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    public static final int USER_PER_PAGE = 4;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUser(){
        List<User> users = this.userRepository.findAll(Sort.by("firstName").ascending());

        return users;
    }

    @Override
    public List<Role> listRoles() {
        List<Role> roles = this.roleRepository.findAll();
        return roles;
    }

    @Override
    public User getByEmail(String email) {
        User user = this.userRepository.findByEmail(email);
        return user;
    }


    @Override
    public User save(User user) {
        boolean isUpdating = (user.getId() != null);

        if(isUpdating){
            User existingUser = this.userRepository.findById(user.getId()).get();

            if(user.getPassword().isEmpty()){ // password mai koi change nahi h=aya toh wahi purana password dal do
                user.setPassword(existingUser.getPassword());
            }else {
                this.setPasswordEncoder(user); // encoding password before saving into DB
            }
        }else { //  just creating new user
            this.setPasswordEncoder(user); // encoding password before saving into DB

        }
        return this.userRepository.save(user);
    }

    @Override
    public User updateAccount(User userInForm){
        User userInDB = this.userRepository.findById(userInForm.getId()).get();

        if(userInForm.getPassword() != null){
            userInDB.setPassword(userInForm.getPassword());
            setPasswordEncoder(userInDB);
        }

        if(userInForm.getPhotos() != null){
            userInDB.setPhotos(userInForm.getPhotos());
        }
        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());

        return this.userRepository.save(userInDB);
    }
    public void setPasswordEncoder(User user){
        String encode = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
    }

    @Override
    public boolean isEmailUnique( Integer id,String email){
        User userByEmail = this.userRepository.findByEmail(email);
        if (userByEmail == null) return true; // pehle se nahi hai

        boolean isCreatingNew = (id == null);

        if(isCreatingNew){
            if(userByEmail != null) return false;
        }else {
            if (userByEmail.getId() != id){
                return false;
            }
        }
        return true; // agar empty hai toh means ki is email se koi user nahi hai, means unique hai
    }

    // ------------- to update the user -------------------
    @Override
    public Optional<User> get(Integer id) {
//        try {
//            return this.userRepository.findById(id).get();
//
//        }catch (NoSuchElementException e){
//            throw new UserNotFoundException("User not found with id " +id); //  this is custom exception
//        }
        return this.userRepository.findById(id);
    }

    //---------------------- Delete User -------------------
    @Override
    public void deleteUser(Integer id) throws UserNotFoundException {
        Long countById = this.userRepository.countById(id);

        if(countById == null || countById == 0){
            throw new UserNotFoundException("Could not find any user with id " + id);
        }

        this.userRepository.deleteById(id);

    }
     //------------------ Update Enbaled status -----------------
    @Override
    public void updateUSerEnabledStatus(Integer id, boolean enabled) {
        this.userRepository.updateEnabledStatus(id, enabled);
    }

    // ---------------------- Pagining ----------------------
    @Override
    public Page<User> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {

        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, USER_PER_PAGE, sort);// page number starts at 0

        if(keyword != null){
            return this.userRepository.findAll(keyword, pageable);
        }

        Page<User> page = this.userRepository.findAll(pageable);
        return page;

    }




}

