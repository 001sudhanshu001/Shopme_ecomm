package com.ShopMe;

import com.ShopMe.DAO.OrderRepo;
import com.ShopMe.Entity.Customer;
import com.ShopMe.Entity.OrderTrack;
import com.ShopMe.Entity.order.Order;
import com.ShopMe.Entity.Product;
import com.ShopMe.Entity.order.OrderDetail;
import com.ShopMe.Entity.order.OrderStatus;
import com.ShopMe.Entity.order.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderRepoTests {
    @Autowired
    private OrderRepo repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateNewOrderWithSingleProduct() {
        Customer customer = entityManager.find(Customer.class, 6);
        Product product = entityManager.find(Product.class, 1);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);

        mainOrder.copyAddressFromCustomer();

        mainOrder.setShippingCost(10);
        mainOrder.setProductCost(product.getCost());
        mainOrder.setTax(0);
        mainOrder.setSubtotal(product.getPrice());
        mainOrder.setId(product.getId() + 10);

        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setStatus(OrderStatus.NEW);
        mainOrder.setDeliveryDate(new Date());
        mainOrder.setDeliveryDays(3);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setProductCost(10);
        orderDetail.setShippingCost(10);
        orderDetail.setQuantity(1);
        orderDetail.setSubtotal(product.getPrice());
        orderDetail.setUnitPrice(product.getPrice());

        mainOrder.getOrderDetails().add(orderDetail);

        Order savedOrder = repo.save(mainOrder);

        assertThat(savedOrder.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewOrderWithMultipleProduct() {
        Customer customer = entityManager.find(Customer.class, 6);
        Product product1 = entityManager.find(Product.class, 1);
        Product product2 = entityManager.find(Product.class, 2);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();


        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProduct(product1);
        orderDetail1.setOrder(mainOrder);
        orderDetail1.setProductCost(10);
        orderDetail1.setShippingCost(10);
        orderDetail1.setQuantity(1);
        orderDetail1.setSubtotal(product1.getPrice());
        orderDetail1.setUnitPrice(product1.getPrice());

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setProduct(product2);
        orderDetail2.setOrder(mainOrder);
        orderDetail2.setProductCost(10);
        orderDetail2.setShippingCost(10);
        orderDetail2.setQuantity(2);
        orderDetail2.setSubtotal(product2.getPrice());
        orderDetail2.setUnitPrice(product2.getPrice());

        mainOrder.getOrderDetails().add(orderDetail1);
        mainOrder.getOrderDetails().add(orderDetail2);


        mainOrder.setShippingCost(10);
        mainOrder.setProductCost(product1.getPrice() + product2.getPrice());
        mainOrder.setTax(0);
        float subtotal = product1.getPrice() + product2.getPrice() * 2;
        mainOrder.setSubtotal(subtotal);
        mainOrder.setTotal(subtotal + 30);
        mainOrder.setId(product1.getId() + 10);
        mainOrder.setPaymentMethod(PaymentMethod.COD);
        mainOrder.setStatus(OrderStatus.PROCESSING);
        mainOrder.setDeliveryDate(new Date());
        mainOrder.setDeliveryDays(3);

        Order savedOrder = repo.save(mainOrder);

        assertThat(savedOrder.getId()).isGreaterThan(0);
    }

    @Test
    public void testUpdateOrder() {
        Integer orderId = 4;
        Order order = repo.findById(orderId).get();

        order.setStatus(OrderStatus.SHIPPING);
        order.setPaymentMethod(PaymentMethod.COD);
        order.setOrderTime(new Date());
        order.setDeliveryDays(2);

        Order updatedOrder = repo.save(order);

        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }


    @Test
    public void testGetOrder() {
        Integer orderId = 4;
        Order order = repo.findById(orderId).get();

        assertThat(order).isNotNull();
        System.out.println(order);
    }


    @Test
    public void testDeleteOrder() {
        Integer orderId = 3;
        repo.deleteById(orderId);

        Optional<Order> result = repo.findById(orderId);
        assertThat(result).isNotPresent();
    }


    @Test
    public void testUpdateOrderTracks() {
        Integer orderId = 5;
        Order order = repo.findById(orderId).get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setUpdatedTime(new Date());
        newTrack.setStatus(OrderStatus.PICKED);
        newTrack.setNotes(OrderStatus.PICKED.defaultDescription());
//
//        OrderTrack processingTrack = new OrderTrack();
//        processingTrack.setOrder(order);
//        processingTrack.setUpdatedTime(new Date());
 //       processingTrack.setStatus(OrderStatus.PROCESSING);
//        processingTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
//        orderTracks.add(processingTrack);
//
        Order updatedOrder = repo.save(order);
//
        System.out.println("-----------------------------------");
        System.out.println(updatedOrder.getOrderTracks().size());
    }


}
