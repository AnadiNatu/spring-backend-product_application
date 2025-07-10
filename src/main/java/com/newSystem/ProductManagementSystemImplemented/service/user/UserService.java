package com.newSystem.ProductManagementSystemImplemented.service.user;

import com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO.*;
import com.newSystem.ProductManagementSystemImplemented.dto.authenticationDTO.UserDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOrderRepository orderRepository;
    @Autowired
    private OrderLogRepository orderLogRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AppMapper mapper;


    public ProductDTO createProduct(CreateProductDTO createProductDTO){
        try {
            Products product = mapper.fromCreateProductDTO(createProductDTO);

            Products savedProduct = productRepository.save(product);

            return mapper.toProductDTO(savedProduct);
        }catch(Exception ex){
            throw new RuntimeException("Failed to create product: " + ex.getMessage());
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


    public List<ProductOrderDTO> getOrdersByProductName(String productName){
        try {
            Products products = productRepository.findByProductNameIgnoreCase(productName).orElseThrow(() -> new ProductEntityNotFoundException("Product with uch name doesn't exist"));

            Long productId = products.getId();

            List<ProductOrder> orders = orderRepository.findAllProductOrderByProductId(productId);

            return orders.stream().map(order -> mapper.toProductOrderDTO(order))
                    .collect(Collectors.toList());

        }catch(Exception ex){
            throw new OrderEntityNotFoundException("Failed to fetch orders by product name: " + ex.getMessage());
        }
    }

    public UserDTO getUserById(Long id){

        try {
            Users users = userRepository.findById(id).orElseThrow(() -> new UserEntityNotFoundException("User not Found"));

            return mapper.toUserDTO(users);
        }catch (UserEntityNotFoundException ex){
            throw ex;
        }catch (Exception ex){
            throw new UserWithIdNotFoundException("Failed to retrieve user: " + ex.getMessage());
        }
    }

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

    public void deleteProduct(String productName){
        try {
            Products products = productRepository.findByProductNameIgnoreCase(productName).orElseThrow(() -> new RuntimeException("Product not found"));

            productRepository.deleteById(products.getId());

        }catch (Exception ex){
            throw new OrderEntityNotFoundException("Order not found for deletion");
        }
    }

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

    public List<ProductDTO> gettingProductsInAscendingOrder(){

        try{
            return productRepository.findAllByOrderByPriceAsc().stream().map(product -> mapper.toProductDTO(product)).toList();
        }catch (ProductEntityNotFoundException ex){
            throw new ProductEntityNotFoundException("Product List Not Found");
        }
    }

    public List<ProductDTO> gettingProductsInDescendingOrder(){

        try{
            return productRepository.findAllByOrderByPriceDesc().stream().map(product -> mapper.toProductDTO(product)).toList();
        }catch (ProductEntityNotFoundException ex){
            throw new ProductEntityNotFoundException("Product List Not Found");
        }
    }

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





















//    public void deleteOrder(Long userId , String productName) throws OrderDeletionErrorException {
////      RuntimeException - OrderEntityNotFoundException
////      RuntimeException - OrderDeletionErrorException
//        try {
//            Users user = userRepository.findById(userId).orElseThrow(() -> new UserEntityNotFoundException("User with Id " +userId+ " was not found"));
//            ProductOrder productOrder = orderRepository.findByUserIdAndProductName(userId, productName).orElseThrow(() -> new OrderByUserForProductException("Product Order For Product : " +productName+ "made by User : " +user.getFname() +" " +user.getLname() +" was not found"));
//
//            if (productOrder == null){
//                throw new OrderEntityNotFoundException("Order not found for deletion");
//            }
//
//            orderRepository.deleteById(productOrder.getOrderId());
//        }catch (Exception ex){
//            throw new OrderDeletionErrorException("Failed to delete order: " + ex.getMessage());
//        }
//    }

}
