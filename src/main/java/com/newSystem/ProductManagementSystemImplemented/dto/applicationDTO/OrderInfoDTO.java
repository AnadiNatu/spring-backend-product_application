package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;

import com.newSystem.ProductManagementSystemImplemented.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderInfoDTO {

    private Long orderId;
    private Date orderDate;
    private Date estimateDeliveryDate;
    private Date deliveryDate;
    private int orderQuantity;
    private OrderStatus orderStatus;
    private boolean lateDeliveryStatus;
    private double orderPrice;
    private String productName;
    private String userName;

}
