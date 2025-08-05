package com.newSystem.ProductManagementSystemImplemented.mapper;

import com.newSystem.ProductManagementSystemImplemented.dto.OrderLogInfoDTO;
import com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO.*;
import com.newSystem.ProductManagementSystemImplemented.dto.authenticationDTO.UserDTO;
import com.newSystem.ProductManagementSystemImplemented.enitity.OrderLog;
import com.newSystem.ProductManagementSystemImplemented.enitity.ProductOrder;
import com.newSystem.ProductManagementSystemImplemented.enitity.Products;
import com.newSystem.ProductManagementSystemImplemented.enitity.Users;
import com.newSystem.ProductManagementSystemImplemented.enums.OrderStatus;
import com.newSystem.ProductManagementSystemImplemented.respository.ProductOrderRepository;
import com.newSystem.ProductManagementSystemImplemented.respository.ProductRepository;
import com.newSystem.ProductManagementSystemImplemented.respository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AppMapper {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductOrderRepository orderRepository;


    public Products fromCreateProductDTO(CreateProductDTO dto){

        Products product = new Products();
        product.setProductName(dto.getProductName());
        product.setProductDesc(dto.getProductDesc());
        product.setProductInventory(dto.getProductInventory());
        product.setPrice(dto.getPrice());

        return product;
    }

    public ProductOrder fromCreateProductOrderDTO(CreateProductOrderDTO dto , Users user , Products product){

        ProductOrder order = new ProductOrder();
        order.setOrderDate(LocalDateTime.now());
        order.setEstimateDeliveryDate((dto.getEstimateDeliveryDate()));
        order.setDeliveryDate((dto.getDeliveryDate()));
        order.setOrderQuantity(dto.getOrderQuantity());
        order.setOrderStatus(OrderStatus.valueOf(dto.getOrderStatus().toUpperCase()));
        order.setOrderPrice(product.getPrice() * dto.getOrderQuantity());
        order.setUsers(user);
        order.setProducts(product);

        return order;
    }

    public ProductOrderDTO toProductOrderDTO(ProductOrder order) {
        return ProductOrderDTO.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate() != null ? Date.from(order.getOrderDate().atZone(ZoneId.systemDefault()).toInstant()) : null)
                .orderQuantity(order.getOrderQuantity())
                .estimateDeliveryDate(toDate(order.getEstimateDeliveryDate()))
                .deliveryDate(toDate(order.getDeliveryDate()))
                .userName(order.getUsers().getFname() + " " + order.getUsers().getLname())
                .userId(order.getUsers().getId())
                .productName(order.getProducts().getProductName())
                .productId(order.getProducts().getId())
                .build();
    }

    public ProductDTO toProductDTO(Products product){

        List<Long> orderIds = product
                .getProductOrders()
                .stream()
                .map(ProductOrder::getOrderId)
                .toList();

        return ProductDTO.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .productDesc(product.getProductDesc())
                .productInventory(product.getProductInventory())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .productOrderIds(orderIds)
                .build();

    }

    public OrderLogInfoDTO toOrderLogInfoDTO(Users user , Products product , ProductOrder productOrder , Date deliveredOn , int productInventory , double totalOrderPrice){

        OrderLogInfoDTO dto = new OrderLogInfoDTO();

        dto.setName(user.getFname() + user.getLname());
        dto.setProductName(product.getProductName());
        dto.setOrderId(productOrder.getOrderId());
        dto.setDeliveredOn(deliveredOn);
        dto.setProductInventory(productInventory);
        dto.setTotalOrderPrice(totalOrderPrice);

        return dto;

    }

    public OrderInfoDTO toOrderInfoDTO(ProductOrder order){

        return OrderInfoDTO.builder()
                .orderId(order.getOrderId())
                .orderDate(toDate(order.getOrderDate()))
                .estimateDeliveryDate(toDate(order.getEstimateDeliveryDate()))
                .deliveryDate(toDate(order.getDeliveryDate()))
                .orderQuantity(order.getOrderQuantity())
                .orderStatus(order.getOrderStatus())
                .orderPrice(order.getOrderPrice())
                .productName(order.getProducts().productName)
                .userName(order.getUsers().getFname() + " " + order.getUsers().getFname())
                .build();
    }

    public UserDTO toUserDTO(Users user) {
        return UserDTO.builder()
                .fname(user.getFname())
                .lname(user.getLname())
                .email(user.getEmail())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRoles())
                .build();
    }

    public UserInfoDTO toUserInfoDto(Users user){

        return UserInfoDTO
                .builder()
                .fname(user.getFname())
                .lname(user.getLname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .orderIds(getAllOrderIds(user))
                .build();

    }

    public OrderLogDTO toOrderLogDto(OrderLog log , Users user , Products product){

        if (log == null){
            return null;
        }

        OrderLogDTO dto = new OrderLogDTO();

        dto.setOrderId(log.getProductOrder() != null ? log.getProductOrder().getOrderId() : null);

        Products resolvedProduct = product != null ? product : log.getProduct();
        dto.setProductName(resolvedProduct != null ? resolvedProduct.getProductName() : null);

        Users resolvedUser = user != null ? user : log.getUser();
        dto.setUserName(resolvedUser != null ? resolvedUser.getFname() + " " + resolvedUser.getLname() : null);

        ProductOrder order = log.getProductOrder();
        dto.setOrderQuantity(order != null ? order.getOrderQuantity() : 0);
        dto.setOrderPrice(order != null ? order.getOrderPrice() : 0.0);
        dto.setOrderStatus(order != null ? order.getOrderStatus() : null);
        dto.setProductInventory(log.getProductInventory());
        dto.setOrderQuantity(order != null ? order.getOrderQuantity() : 0);

        return dto;
    }

    public ProductDetailAfterOrderDTO toProductDetailAfterOrderDTO(Products product){

        return new ProductDetailAfterOrderDTO(
                product.getId(),
                product.getProductName(),
                product.getProductInventory()
        );
    }

//    public OrderDetailsDTO toOrderDetailsDTO(ProductOrder order) {
//        return new OrderDetailsDTO(
//                order.getOrderId(),
//                toDate(order.getEstimateDeliveryDate()),
//                toDate(order.getDeliveryDate()),
//                order.getOrderQuantity(),
//                order.getOrderStatus(),
//                order.getOrderPrice(),
//                order.getProducts().getProductName(),
//                order.getProducts().getPrice()
//        );
//    }

    public OrderProductListDTO toOrderProductListDTO(ProductOrder order){

        return new OrderProductListDTO(
                order.getProducts().getProductName(),
                order.getProducts().getPrice(),
                order.getOrderQuantity()
        );

    }

    public OrderSummaryDTO toOrderSummaryDTO(ProductOrder order) {
        List<OrderProductListDTO> productList = Collections.singletonList(toOrderProductListDTO(order));
        return new OrderSummaryDTO(
                order.getOrderId(),
                order.getOrderDate(),
                order.getOrderStatus(),
                order.getOrderPrice(),
                productList
        );
    }



    public Date toDate(LocalDateTime localDateTime){
                return localDateTime != null ? Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    public LocalDateTime toLocalDateTime(Date date){
        return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

//    --------------
    public List<String> getProductNameFromOrders(List<ProductOrder> orders){
        return orders
                .stream()
                .map(order -> order.getProducts().getProductName())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Long> getProductIdsFromOrders(List<ProductOrder> orders){

        return orders.stream()
                .map(order -> order.getProducts().getId())
                .distinct()
                .collect(Collectors.toList());

    }

    public List<Long> getOrderIdsByProduct(List<ProductOrder> orders , String productName){

        return orders.stream()
                .filter(order -> order.getProducts().getProductName().equalsIgnoreCase(productName))
                .map(ProductOrder::getOrderId)
                .collect(Collectors.toList());
    }

    public List<ProductOrder> getAllOrdersByUser(List<ProductOrder> orders , String userName){

        return orders.stream()
                .filter(order -> (order.getUsers().getLname().equalsIgnoreCase(userName) || order.getUsers().getFname().equalsIgnoreCase(userName)))
                .collect(Collectors.toList());

    }

    public List<Products> getAllProductsOrderByUser(List<ProductOrder> orders , Long userId){

        return orders
                .stream()
                .filter(order -> order.getUsers().getId().equals(userId))
                .map(ProductOrder::getProducts)
                .distinct()
                .toList();
    }

    public List<Long> getAllOrderIds(Users user){

        return orderRepository
                .findAll()
                .stream()
                .filter(order -> order.getUsers().getEmail().equalsIgnoreCase(user.getEmail()))
                .map(ProductOrder::getOrderId)
                .toList();

    }
}

