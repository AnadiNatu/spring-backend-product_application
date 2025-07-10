package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private Long productId;
    private  String productName;
    private  String productDesc;
    private int productInventory;
    private double price ;
    private List<Long> productOrderIds;

}
