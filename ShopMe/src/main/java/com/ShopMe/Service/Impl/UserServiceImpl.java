package com.ShopMe.Service.Impl;

import com.ShopMe.DAO.RoleRepository;
import com.ShopMe.DAO.UserRepository;
import com.ShopMe.Entity.Role;
import com.ShopMe.Entity.User;
import com.ShopMe.ExceptionHandler.UserNotFoundException;
import com.ShopMe.Service.UserService;
import com.ShopMe.UtilityClasses.AmazonS3Util;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    public static final int USER_PER_PAGE = 4;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AmazonS3Util amazonS3Util;


    @Override
    public List<User> getAllUser(){
        return this.userRepository.findAll(Sort.by("firstName").ascending());
    }

    @Override
    public List<Role> listRoles() {
        return this.roleRepository.findAll();
    }

    @Override
    public User getByEmail(String email) {
        User user = this.userRepository.findByEmail(email);
        user.setPreSignedURL(amazonS3Util.generatePreSignedUrl("user-photos/"
                + user.getId() + "/" + user.getPhotos()));

        return user;
    }


    @Override
    public User save(User user) {
        boolean isUpdating = (user.getId() != null);

        if(isUpdating){
            User existingUser = this.userRepository.findById(user.getId()).get();

            if(user.getPassword().isEmpty()){
                user.setPassword(existingUser.getPassword());
            }else {
                this.setPasswordEncoder(user);
            }
        }else {
            this.setPasswordEncoder(user);
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
    private void setPasswordEncoder(User user){
        String encode = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
    }

    @Override
    public boolean isEmailUnique( Integer id,String email){
        User userByEmail = this.userRepository.findByEmail(email);
        if (userByEmail == null) return true;

        boolean isCreatingNew = (id == null);

        if(isCreatingNew){
            if(userByEmail != null) return false;
        }else {
            return Objects.equals(userByEmail.getId(), id);
        }
        return true;
    }

    @Override
    public Optional<User> get(Integer id) {
        Optional<User> optionalUser = this.userRepository.findById(id);
        if(optionalUser.isEmpty()) {
            return optionalUser;
        } else {
            User user = optionalUser.get();
            user.setPreSignedURL(amazonS3Util.generatePreSignedUrl("user-photos/"
                    + user.getId() + "/" + user.getPhotos()));
            return Optional.of(user);
        }
    }

    @Override
    public void deleteUser(Integer id) throws UserNotFoundException {
        Long countById = this.userRepository.countById(id);

        if(countById == null || countById == 0){
            throw new UserNotFoundException("Could not find any user with id " + id);
        }
        this.userRepository.deleteById(id);
    }

    @Override
    public void updateUSerEnabledStatus(Integer id, boolean enabled) {
        this.userRepository.updateEnabledStatus(id, enabled);
    }

    @Override
    public Page<User> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {

        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, USER_PER_PAGE, sort);

        if(keyword != null){
            Page<User> userPage = this.userRepository.findAll(keyword, pageable);
            addResignedURI(userPage);
            return userPage;
        }

        // TODO :: It will also Fetch Password, So use DTO
        Page<User> userPage = this.userRepository.findAll(pageable);

        addResignedURI(userPage);
        return userPage;
    }

    private void addResignedURI(Page<User> userPage) {
        for(User user : userPage) {
            if(user.getPhotos() != null && !user.getPhotos().isEmpty()) {
                String resignedUrl =
                        amazonS3Util.generatePreSignedUrl("user-photos/"
                                + user.getId() + "/" + user.getPhotos());
                user.setPreSignedURL(resignedUrl);
            }
        }
    }

}

