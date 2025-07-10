package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateProductOrderDTO {


    private LocalDateTime estimateDeliveryDate;
    private LocalDateTime deliveryDate;
    private String productName;
    private String orderStatus;
    private int orderQuantity;

}
