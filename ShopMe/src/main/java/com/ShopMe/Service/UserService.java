package com.ShopMe.Service;

import com.ShopMe.Entity.Role;
import com.ShopMe.Entity.User;
import com.ShopMe.ExceptionHandler.UserNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUser();

    List<Role> listRoles();

    User save(User user);

    public boolean isEmailUnique(Integer id,String email);

    Optional<User> get(Integer id);

    void deleteUser(Integer id) throws UserNotFoundException;

    void updateUSerEnabledStatus(Integer id, boolean enabled);

    Page<User> listByPage(int pageNumber, String sortField, String sortDir, String keyword);

    User getByEmail(String email);

    User updateAccount(User userInForm);
}
