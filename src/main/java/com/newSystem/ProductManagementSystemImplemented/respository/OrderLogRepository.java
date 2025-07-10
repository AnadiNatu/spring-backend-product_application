package com.newSystem.ProductManagementSystemImplemented.respository;

import com.newSystem.ProductManagementSystemImplemented.enitity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {


    @Query("SELECT o FROM OrderLog o JOIN o.user u WHERE u.id = :userId")
    List<OrderLog> findAllOrderLogByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM OrderLog o JOIN o.product p WHERE p.id = :productId")
    List<OrderLog> findAllOrderLogByProductId(@Param("productId") Long productId);

    @Query("SELECT o FROM OrderLog o JOIN o.productOrder po WHERE po.orderId = :orderId")
    Optional<OrderLog> findOrderLogByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT o FROM OrderLog o WHERE o.deliveredOn BETWEEN :startDate AND :endDate")
    List<OrderLog> findAllOrderLogsDeliveredBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT o FROM OrderLog o JOIN o.user u JOIN o.productOrder po WHERE u.id = :userId AND po.orderId = :orderId")
    Optional<OrderLog> findByUserIdAndProductOrderId(@Param("userId") Long userId, @Param("orderId") Long orderId);

    @Query("SELECT o FROM OrderLog o ORDER BY o.deliveredOn DESC")
    List<OrderLog> findAllOrderLogsByDeliveredOnDesc();

}
