package com.ShopMe.Entity.order;

import com.ShopMe.Entity.Product;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_details")
@Getter @Setter
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int quantity;
    private float productCost;
    private float shippingCost;
    private float unitPrice;
    private float subtotal;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
