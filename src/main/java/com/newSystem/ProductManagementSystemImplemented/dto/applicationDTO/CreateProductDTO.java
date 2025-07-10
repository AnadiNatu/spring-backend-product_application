package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDTO {

    public String productName;
public String productDesc;
    public int productInventory;
    public double price ;

}
