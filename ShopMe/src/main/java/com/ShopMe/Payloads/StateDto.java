package com.ShopMe.Payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class StateDto {
    // We are using Dto because State Entity has reference to Country
    // But in view we want to use just id and name of state
    private Integer id;
    private String name;

}
