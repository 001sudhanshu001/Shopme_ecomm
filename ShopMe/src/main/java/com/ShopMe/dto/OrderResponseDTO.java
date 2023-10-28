package com.ShopMe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class OrderResponseDTO {

    private Integer orderId;
    private String status;

}