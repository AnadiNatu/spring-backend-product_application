package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductDTO {

    private String productName;
    private String productDesc;
    private String productInventory;
    private int price;

}
// Use a map on the frontend side to make a dynamic dto with only the field changed for update