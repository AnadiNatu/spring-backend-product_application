package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoDTO {

    private Long orderId;
    private Date orderDate;
    private int quantity;
    private String productName;
    private String userEmail;

}
