package com.ShopMe;

import com.ShopMe.DAO.BrandRepo;
import com.ShopMe.DAO.RoleRepository;
import com.ShopMe.DAO.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class ShopMeApplication implements CommandLineRunner {

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;
	private final BrandRepo brandRepo;
	public static void main(String[] args) {
		SpringApplication.run(ShopMeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//
//		User user = new User("my@gmail.com","546njsxnnj","Ashu","Ashu");
//
//		this.userRepository.save(user);

//		Role roleSalesperson = new Role("SalesPerson", "manage product price , " +
//				"customers, shipping, orders and sales report");
//
//		Role roleEditor = new Role("Editor", "manage categories, brand, products, articles, menus");
//
//		Role roleShipper = new Role("Shipper", "View products, view orders, and update order status");
//
//		Role admin = new Role("Admin", "Manage everything");
//
//		this.roleRepository.saveAll(List.of( admin));

	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

//	@EventListener(ApplicationStartedEvent.class)
//	public void onStart() {
//		System.out.println("Application Is Started...Do Something Here");
//	}
//
//	@EventListener(ContextClosedEvent.class)
//	public void onStop() {
//		System.out.println("I am Closing Application...Hurry and do something");
//	}
}
