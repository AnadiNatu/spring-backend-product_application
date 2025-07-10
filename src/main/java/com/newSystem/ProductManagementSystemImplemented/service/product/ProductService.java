package com.newSystem.ProductManagementSystemImplemented.service.product;


import com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO.CreateProductDTO;
import com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO.ProductDTO;
import com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO.ProductInventoryDTO;
import com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO.UpdateProductDTO;
import com.newSystem.ProductManagementSystemImplemented.enitity.Products;
import com.newSystem.ProductManagementSystemImplemented.exception.IncorrectProductNameException;
import com.newSystem.ProductManagementSystemImplemented.exception.OrderEntityNotFoundException;
import com.newSystem.ProductManagementSystemImplemented.exception.ProductEntityNotFoundException;
import com.newSystem.ProductManagementSystemImplemented.mapper.AppMapper;
import com.newSystem.ProductManagementSystemImplemented.respository.OrderLogRepository;
import com.newSystem.ProductManagementSystemImplemented.respository.ProductOrderRepository;
import com.newSystem.ProductManagementSystemImplemented.respository.ProductRepository;
import com.newSystem.ProductManagementSystemImplemented.respository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {


    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductOrderRepository orderRepository;
    private final OrderLogRepository logRepository;
    private final AppMapper mapper;


//    Creating a Product
public ProductDTO createProduct(CreateProductDTO createProductDTO){
    try {
        Products product = mapper.fromCreateProductDTO(createProductDTO);

        Products savedProduct = productRepository.save(product);

        return mapper.toProductDTO(savedProduct);
    }catch(Exception ex){
        throw new RuntimeException("Failed to create product: " + ex.getMessage());
    }
}

//    Getting all the product
public List<ProductDTO> getAllProducts(){
    try {
        List<Products> products = productRepository.findAll();
        List<ProductDTO> productDTOList = new ArrayList<>();
        //          RuntimeException - ProductEntityNotFoundException

        for (Products products1 : products) {
            productDTOList.add(mapper.toProductDTO(products1));
        }
        return productDTOList;
    }catch (Exception ex){
        throw new ProductEntityNotFoundException("Failed to fetch users: " + ex.getMessage());
    }
}


//    Updating the product details
public ProductDTO updateProduct(UpdateProductDTO productDTO){
    try {
        Products products = productRepository.findByProductNameIgnoreCase(productDTO.getProductName()).orElseThrow(() -> new ProductEntityNotFoundException("Product with such name doesn't exist"));

        products.setProductDesc(productDTO.getProductDesc());
        products.setProductInventory(products.productInventory);
        products.setPrice(productDTO.getPrice());

        Products updateProduct = productRepository.save(products);

        return mapper.toProductDTO(updateProduct);

    }catch (Exception ex){
        throw new IncorrectProductNameException("Failed to update product: " + ex.getMessage() );
    }
}

//    Deleting product by product names
public void deleteProduct(String productName){
    try {
        Products products = productRepository.findByProductNameIgnoreCase(productName).orElseThrow(() -> new RuntimeException("Product not found"));

        productRepository.deleteById(products.getId());

    }catch (Exception ex){
        throw new OrderEntityNotFoundException("Order not found for deletion");
    }
}


//    Getting all the product by Ascending Order
public List<ProductDTO> gettingProductsInAscendingOrder(){

    try{
        return productRepository.findAllByOrderByPriceAsc().stream().map(product -> mapper.toProductDTO(product)).toList();
    }catch (ProductEntityNotFoundException ex){
        throw new ProductEntityNotFoundException("Product List Not Found");
    }
}

//    Getting all the product by Descending Order
    public List<ProductDTO> gettingProductsInDescendingOrder(){

        try{
            return productRepository.findAllByOrderByPriceDesc().stream().map(product -> mapper.toProductDTO(product)).toList();
        }catch (ProductEntityNotFoundException ex){
            throw new ProductEntityNotFoundException("Product List Not Found");
        }
    }

    //  Get Top-N Most Ordered Products
    public List<ProductDTO> getTopOrderedProducts(int topN){

        List<Object[]> results = orderRepository.findTopOrderProducts(PageRequest.of(0 , topN));
        return results
                .stream()
                .map(row -> {
                    Products product = (Products) row[0];
                    return mapper.toProductDTO(product);
                })
                .collect(Collectors.toList());

    }

    // Get Inventory Status of All Products
    public List<ProductInventoryDTO> getInventoryStatus(){

        return productRepository
                .findAll()
                .stream()
                .map(p-> new ProductInventoryDTO(p.getProductName() , p.getProductInventory()))
                .collect(Collectors.toList());

    }

//    Getting product By Id
    public ProductDTO gettingProductById(Long id){

        return productRepository.findById(id).map(mapper::toProductDTO).orElseThrow(() -> new ProductEntityNotFoundException("Product with Id " +id+ "was not found"));

    }
}
