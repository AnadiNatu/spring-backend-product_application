package com.newSystem.ProductManagementSystemImplemented.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderLogInfoDTO {

    private String name;
    private String productName;
    private Long orderId;
    private Date deliveredOn;
    private int productInventory;
    private double totalOrderPrice;

}
