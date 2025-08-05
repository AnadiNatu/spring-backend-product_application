package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDTO {
    public String productName;
    public String productDesc;
    public int productInventory;
    public double price ;
    private MultipartFile imageFile;
}
