package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;

import com.newSystem.ProductManagementSystemImplemented.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDTO {

    private Long orderId;
    private Date estimateDeliveryDate;
    private Date deliiveryDate;
    private int orderQuantity;
    private OrderStatus orderStatus;
    private double orderPrice;
    private String productName;
    private double productPrice;

}
