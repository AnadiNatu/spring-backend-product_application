package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductOrderDTO {

    private  Long orderId;
    private Date orderDate;
    private int orderQuantity;
    private Date estimateDeliveryDate;
    private Date deliveryDate;
    private String userName;
    private Long userId;
    private String productName;
    private Long productId;
}
