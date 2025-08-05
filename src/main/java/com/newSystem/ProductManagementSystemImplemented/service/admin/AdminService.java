package com.newSystem.ProductManagementSystemImplemented.service.admin;

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
import com.newSystem.ProductManagementSystemImplemented.service.image_storage.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOrderRepository orderRepository;
    @Autowired
    private OrderLogRepository orderLogRepository;
    @Autowired
    private AppMapper mapper;
    @Autowired
    private JwtUtil jwtUtil;

// Utility functions

    public ProductDTO getProductById(Long id){

        return productRepository.findById(id).map(product ->mapper.toProductDTO(product))
        .orElseThrow(() -> new RuntimeException("Product by this Id" +id+ "not found"));
    }

    public ProductDTO getProductByName(String name){
        return productRepository.findByProductNameIgnoreCase(name).map(product -> mapper.toProductDTO(product)).orElseThrow(() -> new RuntimeException("Product with that name " +name+ "do not exist"));
    }

    public UserInfoDTO getUserById(Long id){
        return userRepository.findById(id).map(user -> mapper.toUserInfoDto(user)).orElseThrow(() -> new RuntimeException("User with id was not found" +id));
    }

    public UserInfoDTO getUserByName(String name){
        return userRepository.findUsersByNameContaining(name).map(user -> mapper.toUserInfoDto(user)).orElseThrow(() -> new RuntimeException("User that name doesn't exist" + name));
    }

    public OrderInfoDTO getProductOrderById(Long id){
        return orderRepository.findById(id).map(order -> mapper.toOrderInfoDTO(order)).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<OrderInfoDTO> getProductOrderByUser_Id(Long userId){
        return orderRepository.findByUsers_Id(userId).stream().map(order -> mapper.toOrderInfoDTO(order)).toList();
    }

    public List<OrderInfoDTO> getProductOrderByProduct_Id(Long productId){
        return orderRepository.findByProducts_Id(productId).stream().map(order -> mapper.toOrderInfoDTO(order)).toList();
    }


    @Autowired
    private ImageStorageService imageStorageService;

    public ProductDTO createProduct(CreateProductDTO createProductDTO , MultipartFile imageFile){
        try {
            Products product = mapper.fromCreateProductDTO(createProductDTO);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Logged in user: " + auth.getName());
            System.out.println("Authorities: " + auth.getAuthorities());
            if (imageFile != null && !imageFile.isEmpty()){
                String imageUrl = imageStorageService.save(imageFile);
                product.setImageUrl(imageUrl);
            }
            Products savedProduct = productRepository.save(product);

            return mapper.toProductDTO(savedProduct);
        }catch(Exception ex){
            throw new RuntimeException("Failed to create product: " + ex.getMessage());
        }
    }


    public OrderLog createOrderLog(Long userId , Long productId , Long orderId , Date deliveredOn , int productInventory , double totalOrderPrice){
        Users userFromLoggedIn = jwtUtil.getLoggedInUser();

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

    public List<UserInfoDTO> getAllUsers(){

        try {
            List<Users> users = userRepository.findAll();
            List<UserInfoDTO> userDTOList = new ArrayList<>();
            for (Users users1 : users) {
                userDTOList.add(mapper.toUserInfoDto(users1));
            }
            return userDTOList;
        }catch(Exception ex){
            throw new UserEntityNotFoundException("Failed to fetch users: " + ex.getMessage());
        }
    }

    public List<ProductDTO> getAllProducts(){

        try {
            List<Products> products = productRepository.findAll();
            List<ProductDTO> productDTOList = new ArrayList<>();

            for (Products products1 : products) {
                productDTOList.add(mapper.toProductDTO(products1));
            }
            return productDTOList;
        }catch (Exception ex){
            throw new ProductEntityNotFoundException("Failed to fetch users: " + ex.getMessage());
        }
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

    public double getTotalRevenueForProduct(String productName){

        Products product = productRepository.findByProductNameIgnoreCase(productName)
                .orElseThrow(() -> new ProductEntityNotFoundException("Product Not Found"));

        List<ProductOrder> orders = orderRepository.findAllProductOrderByProductId(product.getId());

        return orders
                .stream()
                .mapToDouble(order -> order.getOrderQuantity()*product.getPrice())
                .sum();
    }

    // ProductService
    // Get Inventory Status of All Products
    public List<ProductInventoryDTO> getInventoryStatus(){

        return productRepository
                .findAll()
                .stream()
                .map(p-> new ProductInventoryDTO(p.getProductName() , p.getProductInventory()))
                .collect(Collectors.toList());

    }


    // OrderService - UserService
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

    // Get all order log for a product
    public List<OrderLogDTO> getOrderLogByProductId(String productName){

        Products product = productRepository.findByProductNameIgnoreCase(productName).orElseThrow(() -> new ProductEntityNotFoundException("Product Entity Not Found"));
        List<OrderLog> logs = orderLogRepository.findAllOrderLogByProductId(product.getId());

        if (logs.isEmpty()) {
            throw new RuntimeException("No order logs found for Product ID: " + product.getId());
        }

        List<OrderLogDTO> logDTOList = new ArrayList<>();
        for (OrderLog log : logs){
            logDTOList.add(mapper.toOrderLogDto(log , null , product));
        }
        return logDTOList;

    }

    // Getting orderLog By Log Id
    public OrderLogDTO getOrderLogByOrderId(Long orderId){
        return orderLogRepository.findOrderLogByOrderId(orderId).map(log -> mapper.toOrderLogDto(log , null , null)).orElseThrow(() -> new RuntimeException("Order Log Not Found"));
    }

    //Getting orderLog By user and product
    public OrderLogDTO getOrderLogUserAndProduct(String productName){

        Users users = userRepository.findByEmail(jwtUtil.getLoggedInUsername()).orElseThrow(() -> new UserEntityNotFoundException("User Not Found"));

        Products products = productRepository.findByProductNameIgnoreCase(productName).orElseThrow(() -> new ProductEntityNotFoundException("No Product Found"));

        OrderLogDTO logList = orderLogRepository.findByUserIdAndProductOrderId(users.getId() , products.getId()).map(logs -> mapper.toOrderLogDto(logs , users , products)).orElseThrow(() -> new OrderByUserForProductException("Order not found"));

        return logList;
    }

    // Get all logs sorted by deliveredOn DESC
    public List<OrderLogDTO> getAllLogsSortedByDeliveryDateDesc(){

        return orderLogRepository.findAllOrderLogsByDeliveredOnDesc().stream().map(logs -> mapper.toOrderLogDto(logs , null , null)).toList();

    }

    // Get all logs between two dates
    public List<OrderLogDTO> getOrderLogsBetweenDates(Date startDate , Date endDate){

        return orderLogRepository.findAllOrderLogsDeliveredBetween(startDate, endDate).stream().map(logs -> mapper.toOrderLogDto(logs , null , null)).toList();

    }


}

