package com.newSystem.ProductManagementSystemImplemented.controller;

import com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO.*;
import com.newSystem.ProductManagementSystemImplemented.dto.authenticationDTO.UserDTO;
import com.newSystem.ProductManagementSystemImplemented.exception.OrderDeletionErrorException;
import com.newSystem.ProductManagementSystemImplemented.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("product")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductDTO productDTO){

        ProductDTO product = userService.createProduct(productDTO);

        if (product == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PostMapping("order")
    public ResponseEntity<?> createOrder(@RequestBody CreateProductOrderDTO orderDTO){

        ProductOrderDTO orders = userService.createProductOrder(orderDTO);

        if (orders == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }else
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(orders);

    }


    @GetMapping("get/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId){

        UserDTO users = userService.getUserById(userId);

        if (users == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }else
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(users);
    }

    @GetMapping("product/all")
    public ResponseEntity<?> getAllProduct(){
        List<ProductDTO> productDTO = userService.getAllProducts();
        if (productDTO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Products Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(productDTO);

    }

    @GetMapping("product/all/{name}")
    public ResponseEntity<?> getAllOrdersByProductName(@PathVariable(name = "name") String productName){

        List<ProductOrderDTO> orderDTOList = userService.getOrdersByProductName(productName);
        if (orderDTOList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Products with such name found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderDTOList);
    }

    @GetMapping("product/user/all/{Id}")
    public ResponseEntity<?> getAllOrdersByUserId(@PathVariable(name = "Id") Long userId){

        List<ProductOrderDTO> orderDTOList = userService.getOrdersByUserId(userId);
        if (orderDTOList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This user has not placed any orders ");
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderDTOList);
    }

    @GetMapping("product/order/all")
    public ResponseEntity<?> getOrdersByProductNameAndUserId(@RequestParam String productName , @RequestParam Long userId){

        ProductOrderDTO  order = userService.getOrdersByProductNameAndUserId(userId,productName);

        if (order == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The user has not placed an order for this product");
        }
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @PutMapping("updateProduct")
    public ResponseEntity<?> updateProduct(@RequestBody UpdateProductDTO productDTO){

        ProductDTO product = userService.updateProduct(productDTO);

        if (product == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not updated");
        }
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PutMapping("updateOrder")
    public ResponseEntity<?> updateOrder(@RequestBody UpdateOrderDTO orderDTO){

        ProductOrderDTO order = userService.updateOrder(orderDTO);
        if (order == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not updated");
        }
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @DeleteMapping("delete/product/{productName}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productName){
        userService.deleteProduct(productName);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("delete/order")
    public ResponseEntity<Void> deleteOrder(@RequestParam Long userId , @RequestParam String productName) throws OrderDeletionErrorException {

        userService.deleteOrder(userId, productName);

        return ResponseEntity.status(HttpStatus.OK).body(null);

    }
}
