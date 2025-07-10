package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;

import com.newSystem.ProductManagementSystemImplemented.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLogDTO {

    private Long orderId;
    private String productName;
    private String userName;
    private int orderQuantity;
    private double orderPrice;
    private OrderStatus orderStatus;
    private Date deliveredOn;
    private int productInventory;
    private int productOrderQuantity;
}
