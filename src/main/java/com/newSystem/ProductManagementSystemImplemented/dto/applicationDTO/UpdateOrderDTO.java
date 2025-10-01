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
public class UpdateOrderDTO {
    private Date orderDate;
    private Date estimateDeliveryDate;
    private Date deliveryDate;
    private int orderQuantity;
    private String productName;
}
// Use a map on the frontend side to make a dynamic dto with only the field changed for update
