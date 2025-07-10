package com.newSystem.ProductManagementSystemImplemented.service.order;

import com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO.*;
import com.newSystem.ProductManagementSystemImplemented.enitity.OrderLog;
import com.newSystem.ProductManagementSystemImplemented.enitity.ProductOrder;
import com.newSystem.ProductManagementSystemImplemented.enitity.Products;
import com.newSystem.ProductManagementSystemImplemented.enitity.Users;
import com.newSystem.ProductManagementSystemImplemented.exception.*;
import com.newSystem.ProductManagementSystemImplemented.mapper.AppMapper;
import com.newSystem.ProductManagementSystemImplemented.respository.OrderLogRepository;
import com.newSystem.ProductManagementSystemImplemented.respository.ProductOrderRepository;
import com.newSystem.ProductManagementSystemImplemented.respository.ProductRepository;
import com.newSystem.ProductManagementSystemImplemented.respository.UserRepository;
import com.newSystem.ProductManagementSystemImplemented.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductOrderRepository orderRepository;
    private final OrderLogRepository orderLogRepository;
    private final AppMapper mapper;
    private final JwtUtil jwtUtil;


    public ProductOrderDTO createProductOrder(CreateProductOrderDTO orderDTO){

        try {
            Users user = jwtUtil.getLoggedInUser();
            Products products = productRepository.findByProductNameIgnoreCase(orderDTO.getProductName()).orElseThrow(() -> new ProductEntityNotFoundException("Product Not Found"));
            ProductOrder productOrder = mapper.fromCreateProductOrderDTO(orderDTO , user , products);

            ProductOrder currentOrder = orderRepository.save(productOrder);

            createOrderLog(
                    user.getId(),
                    products.getId(),
                    currentOrder.getOrderId(),
                    mapper.toDate(orderDTO.getDeliveryDate()), // Converts LocalDateTime to Date
                    products.getProductInventory(),
                    currentOrder.getOrderPrice()
            );

            return mapper.toProductOrderDTO(currentOrder);
        }catch(Exception ex){
            throw new RuntimeException("Failed to create product order: " + ex.getMessage());
        }
    }


    public OrderLog createOrderLog(Long userId , Long productId , Long orderId , Date deliveredOn , int productInventory , double totalOrderPrice){

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        ProductOrder productOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Product Order not found with ID: " + orderId));

        OrderLog orderLog = new OrderLog();
        orderLog.setUser(user);
        orderLog.setProduct(product);
        orderLog.setProductOrder(productOrder);
        orderLog.setDeliveredOn(deliveredOn);
        orderLog.setProductInventory(productInventory);
        orderLog.setTotalOrderPrice(totalOrderPrice);

        return orderLogRepository.save(orderLog);

    }


    public List<ProductOrderDTO> getAllOrders(){
        try {
            List<ProductOrder> orders = orderRepository.findAll();
            List<ProductOrderDTO> orderList = new ArrayList<>();

            for (ProductOrder order : orders) {
                orderList.add(mapper.toProductOrderDTO(order));
            }
            return orderList;
        }catch(Exception ex){
            throw new OrderEntityNotFoundException("Failed to fetch orders: " + ex.getMessage());
        }
    }


    // Getting all the Order by ProductName - Controller Created
    public List<ProductOrderDTO> getOrdersByProductName(String productName){
        try {
            Products products = productRepository.findByProductNameIgnoreCase(productName).orElseThrow(() -> new ProductEntityNotFoundException("Product with uch name doesn't exist"));

            Long productId = products.getId();

            List<ProductOrder> orders = orderRepository.findAllProductOrderByProductId(productId);

            return orders.stream().map(mapper::toProductOrderDTO)
                    .collect(Collectors.toList());

        }catch(Exception ex){
            throw new OrderEntityNotFoundException("Failed to fetch orders by product name: " + ex.getMessage());
        }
    }

    // Getting the orders by User_Id - Controller Created
    public List<ProductOrderDTO> getOrdersByUserId(Long user_id){
        try {
            List<ProductOrder> orders = orderRepository.findByUsers_Id(user_id);
            if (orders.isEmpty()){
                throw new OrderWithIdNotFoundException("No orders found for user ID: " + user_id);
            }

            List<ProductOrderDTO> orderList = new ArrayList<>();
            for (ProductOrder order : orders) {
                orderList.add(mapper.toProductOrderDTO(order));
            }
            return orderList;
        }catch (Exception ex){
            throw new OrderEntityNotFoundException("Failed to fetch orders by user ID: " + ex.getMessage());
        }
    }


    // getting all the orders by product name and user_id - Controller Created
    public ProductOrderDTO getOrdersByProductNameAndUserId(Long user_id , String productName) {
        try {
            ProductOrder orders = orderRepository
                    .findByUserIdAndProductName(user_id, productName).orElseThrow(() -> new OrderByUserForProductException("Order for Product " +productName+ " made by user with Id " +user_id+ " was not found"));

            if (orders == null) {
                throw new OrderByUserForProductException("Order not found for user ID: " + user_id + " and product: " + productName);
            }
            return mapper.toProductOrderDTO(orders);

        }catch (Exception ex){
            throw new OrderEntityNotFoundException("Failed to fetch order by product and user ID: " + ex.getMessage());
        }
    }


    // Updating the details of the order - Controller Created
    public ProductOrderDTO updateOrder(UpdateOrderDTO orderDTO){
        try{
            Products products = productRepository.findByProductNameIgnoreCase(orderDTO.getProductName()).orElseThrow(() -> new ProductEntityNotFoundException("Product not found"));
            Users users = userRepository.findByEmail(jwtUtil.getLoggedInUsername()).orElseThrow(() -> new UserEntityNotFoundException("User not found"));

            ProductOrder orders = orderRepository
                    .findByUserIdAndProductName(users.getId() , products.getProductName())
                    .orElseThrow(() -> new OrderByUserForProductException("Product Order For Product : " +products.getProductName()+ "made by User : " +users.getFname() +" " +users.getLname() +" was not found"));

            if (orders != null){
                throw new OrderByUserForProductException("Order not found for user ID: " + orders.getUsers().getId() + " and product: " + products.getProductName());
            }
            orders.setOrderDate(orderDTO.getOrderDate() != null ? mapper.toLocalDateTime(orderDTO.getOrderDate()) : null);
            orders.setEstimateDeliveryDate(orderDTO.getEstimateDeliveryDate() != null ? mapper.toLocalDateTime(orderDTO.getEstimateDeliveryDate()) : null);
            orders.setDeliveryDate(orderDTO.getEstimateDeliveryDate() != null ? mapper.toLocalDateTime(orderDTO.getDeliveryDate()) : null);
            orders.setOrderQuantity(orderDTO.getOrderQuantity());
            orders.setProducts(products);
            orders.setUsers(users);

            ProductOrder updatedOrder = orderRepository.save(orders);
            return mapper.toProductOrderDTO(updatedOrder);
        }catch(Exception ex){
            throw new OrderNotUpdateException("Failed to update order: " + ex.getMessage());
        }
    }


    // Deleting order by help of userId and productName - Controller Created
    public void deleteOrder(Long userId , String productName) throws OrderDeletionErrorException {
        try {
            ProductOrder productOrder = orderRepository.findByUserIdAndProductName(userId, productName).orElseThrow(() -> new OrderByUserForProductException(""));

            if (productOrder == null){
                throw new OrderEntityNotFoundException("Order not found for deletion");
            }

            orderRepository.deleteById(productOrder.getOrderId());
        }catch (Exception ex){
            throw new OrderDeletionErrorException("Failed to delete order: " + ex.getMessage());
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


    // Get all logs for a user
    public List<OrderLogDTO> getOrderLogsByUser(){

        String userName = jwtUtil.getLoggedInUsername();
        Users users = userRepository.findByEmail(userName).orElseThrow(() -> new UsernameNotFoundException("No User Name found"));
        List<OrderLog> logs = orderLogRepository.findAllOrderLogByUserId(users.getId());

        if (logs.isEmpty()) {
            throw new RuntimeException("No order logs found for user ID: " + users.getId());
        }

        List<OrderLogDTO> logDTOList = new ArrayList<>();
        for (OrderLog log : logs){
            logDTOList.add(mapper.toOrderLogDto(log , users , null));
        }
        return logDTOList;
    }



    //Creating A Product Order
//    Getting order in the ascending order of the quantity
//    Getting order in the ascending order of order date
//    Deleting all the order of the userName

}
