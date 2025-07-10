package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;

import com.newSystem.ProductManagementSystemImplemented.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryDTO {
    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private double totalPrice;
    private List<OrderProductListDTO> products;
}
