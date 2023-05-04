package com.ShopMe;

import com.ShopMe.DAO.UserRepository;
import com.ShopMe.Entity.Role;
import com.ShopMe.Entity.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
@RequiredArgsConstructor
public class UserRepositoryTest {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUser(){
        Role roleAdmin = entityManager.find(Role.class, entityManager);
        User user1 = new User("s@gmail.com","564g","Sudhanshu","Arya");
        user1.addRole(roleAdmin);

        User savedUser = this.userRepository.save(user1);
        assertThat(savedUser.getId()).isGreaterThan(0); // means role actullay inserted into DB

    }

    @Test
    public void testGetUserByEmail(){
        String email = "Sahil@gmail.com";
        User user = this.userRepository.findByEmail(email);

        assertThat(user).isNotNull();
    }

    @Test
    public void testCountById(){
        Integer id = 10;
        Long countById = this.userRepository.countById(id);

        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser(){
        Integer id = 1;
        userRepository.updateEnabledStatus(id,false);
    }

    @Test
    public void testEnableUser(){
        Integer id = 2;
        userRepository.updateEnabledStatus(id,true);
    }


    // paging test
    @Test
    public void testListFirstPage() {
        int pageNumber = 1;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<User> page = this.userRepository.findAll(pageable);

        List<User> listUsers = page.getContent();

        listUsers.forEach(user -> System.out.println(user));

        assertThat(listUsers.size()).isEqualTo(pageSize);
    }

    @Test
    public void testSearchUser(){
        String keyword = "bruce";

        int pageNumber = 0;
        int pageSize = 4;

        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<User> page = this.userRepository.findAll(keyword, pageable); // Custom method

        List<User> listUsers = page.getContent();

        listUsers.forEach(user -> System.out.println(user));

        assertThat(listUsers.size()).isGreaterThan(0);

    }


}
