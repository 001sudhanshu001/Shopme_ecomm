package com.ShopMe;

import com.ShopMe.DAO.RoleRepository;
import com.ShopMe.Entity.Role;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor
@Rollback(false) // means commit the changes after the code runs error less
public class RoleRepositoryTest {

  //  @Autowired
    private final RoleRepository roleRepository;


    @Test
    public void testCreateFirstRole(){
        Role role = new Role("Admin", "manage Everything");

        System.out.println("Isnide testing of roles");
        Role savedRole = this.roleRepository.save(role);
        assertThat(savedRole.getId()).isGreaterThan(0); // means role actullay inserted into DB
    }

    @Test
    public void testCreateRestRoles(){
        Role roleSalesperson = new Role("SalesPerson", "manage product price , " +
                "customers, shipping, orders and sales report");

        Role roleEditor = new Role("Editor", "manage categories, brand, products, articles, menus");

        Role roleShipper = new Role("Shipper", "View products, view orders, and update order status");

        Role roleAssistant = new Role("Assistant", "manage Question and reviews");

        this.roleRepository.saveAll(List.of(roleSalesperson, roleEditor, roleAssistant, roleShipper));


    }
}
