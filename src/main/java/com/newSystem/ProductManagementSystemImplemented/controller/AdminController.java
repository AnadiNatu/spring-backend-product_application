package com.newSystem.ProductManagementSystemImplemented.controller;

import com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO.*;
import com.newSystem.ProductManagementSystemImplemented.exception.OrderDeletionErrorException;
import com.newSystem.ProductManagementSystemImplemented.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/admin/")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("product")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductDTO productDTO){

        ProductDTO product = adminService.createProduct(productDTO);

        if (product == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PostMapping("order")
    public ResponseEntity<?> createOrder(@RequestBody CreateProductOrderDTO orderDTO){
        ProductOrderDTO orders = adminService.createProductOrder(orderDTO);
        if (orders == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }else
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(orders);

    }

    @GetMapping("users")
    public ResponseEntity<?> getAllUsers(){
        List<UserInfoDTO> userDTO = adminService.getAllUsers();
        if (userDTO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Users Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @GetMapping("product/all")
    public ResponseEntity<?> getAllProduct(){
        List<ProductDTO> productDTO = adminService.getAllProducts();
        if (productDTO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Products Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(productDTO);

    }

    @GetMapping("order/all")
    public ResponseEntity<?> getAllOrders(){
        List<ProductOrderDTO> orderDTOList = adminService.getAllOrders();
        if (orderDTOList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Users Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderDTOList);
    }

    @GetMapping("product/all/{name}")
    public ResponseEntity<?> getAllOrdersByProductName(@PathVariable(name = "name") String productName){
        List<ProductOrderDTO> orderDTOList = adminService.getOrdersByProductName(productName);
        if (orderDTOList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Products with such name found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderDTOList);
    }

    @GetMapping("product/user/all/{Id}")
    public ResponseEntity<?> getAllOrdersByUserId(@PathVariable(name = "Id") Long userId){
        List<ProductOrderDTO> orderDTOList = adminService.getOrdersByUserId(userId);
        if (orderDTOList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This user has not placed any orders ");
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderDTOList);
    }

    @GetMapping("product/order/all")
    public ResponseEntity<?> getOrdersByProductNameAndUserId(@RequestParam String productName , @RequestParam Long userId){
        ProductOrderDTO  order = adminService.getOrdersByProductNameAndUserId(userId,productName);
        if (order == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The user has not placed an order for this product");
        }
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @PutMapping("updateProduct")
    public ResponseEntity<?> updateProduct(@RequestBody UpdateProductDTO productDTO){
        ProductDTO product = adminService.updateProduct(productDTO);
        if (product == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not updated");
        }
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PutMapping("updateOrder")
    public ResponseEntity<?> updateOrder(@RequestBody UpdateOrderDTO orderDTO){
        ProductOrderDTO order = adminService.updateOrder(orderDTO);
        if (order == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not updated");
        }
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @DeleteMapping("delete/product/{productName}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productName){
        adminService.deleteProduct(productName);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("delete/order")
    public ResponseEntity<Void> deleteOrder(@RequestParam Long userId , @RequestParam String productName) throws OrderDeletionErrorException {
        adminService.deleteOrder(userId, productName);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/price/asc")
    public ResponseEntity<List<ProductDTO>> getProductsSortedByPriceAsc() {
        return ResponseEntity.ok(adminService.gettingProductsInAscendingOrder());
    }

    @GetMapping("/price/desc")
    public ResponseEntity<List<ProductDTO>> getProductsSortedByPriceDesc() {
        return ResponseEntity.ok(adminService.gettingProductsInDescendingOrder());
    }

    @GetMapping("/top-ordered")
    public ResponseEntity<List<ProductDTO>> getTopOrderedProducts(@RequestParam("topN") int topN) {
        return ResponseEntity.ok(adminService.getTopOrderedProducts(topN));
    }

    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenueForProduct(@RequestParam("productName") String productName) {
        return ResponseEntity.ok(adminService.getTotalRevenueForProduct(productName));
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<ProductInventoryDTO>> getInventoryStatus() {
        return ResponseEntity.ok(adminService.getInventoryStatus());
    }

    @GetMapping("/logs/user")
    public ResponseEntity<List<OrderLogDTO>> getOrderLogsByUser() {
        return ResponseEntity.ok(adminService.getOrderLogsByUser());
    }

    @GetMapping("/logs/product")
    public ResponseEntity<List<OrderLogDTO>> getOrderLogsByProduct(@RequestParam("productName") String productName) {
        return ResponseEntity.ok(adminService.getOrderLogByProductId(productName));
    }

    @GetMapping("/logs/{orderId}")
    public ResponseEntity<OrderLogDTO> getOrderLogByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(adminService.getOrderLogByOrderId(orderId));
    }

    @GetMapping("/logs/user-product")
    public ResponseEntity<OrderLogDTO> getOrderLogByUserAndProduct(@RequestParam("productName") String productName) {
        return ResponseEntity.ok(adminService.getOrderLogUserAndProduct(productName));
    }

    @GetMapping("/logs/sorted/delivery")
    public ResponseEntity<List<OrderLogDTO>> getAllLogsSortedByDeliveryDateDesc() {
        return ResponseEntity.ok(adminService.getAllLogsSortedByDeliveryDateDesc());
    }

    @GetMapping("/logs/between")
    public ResponseEntity<List<OrderLogDTO>> getOrderLogsBetweenDates(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        return ResponseEntity.ok(adminService.getOrderLogsBetweenDates(startDate, endDate));
    }
}
