package com.ShopMe.Controller.order;

import com.ShopMe.Service.Impl.OrderService;
import com.ShopMe.dto.OrderResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderRestController {
    private final OrderService orderService;


    @PostMapping("/orders_shipper/update/{id}/{status}")
    public OrderResponseDTO updateOrderStatus(@PathVariable("id") Integer orderId,
                                              @PathVariable("status") String status) {
        orderService.updateStatus(orderId, status);
        return new OrderResponseDTO(orderId, status);
    }
}
