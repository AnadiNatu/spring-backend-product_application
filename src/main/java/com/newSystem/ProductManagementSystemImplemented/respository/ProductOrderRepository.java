package com.newSystem.ProductManagementSystemImplemented.respository;

import com.newSystem.ProductManagementSystemImplemented.enitity.ProductOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {

    @Query("SELECT po FROM ProductOrder po JOIN po.products p WHERE p.id = :productsId")
    List<ProductOrder> findAllProductOrderByProductId(@Param("productsId") Long productsId);
    @Query("SELECT po FROM ProductOrder po JOIN po.products p WHERE LOWER(p.productName) = LOWER(:productName)")
    Optional<ProductOrder> findProductOrderByProductName(@Param("productName") String productName);
    @Query("SELECT po FROM ProductOrder po JOIN po.users u WHERE u.id = :userId")
    List<ProductOrder> findByUsers_Id(@Param("userId") Long userId);

    @Query("SELECT po FROM ProductOrder po JOIN po.users u JOIN po.products p WHERE u.id = :userId AND LOWER(p.productName) = LOWER(:productName)")
    Optional<ProductOrder> findByUserIdAndProductName(@Param("userId") Long userId , @Param("productName") String productName);

    @Query("SELECT p, COUNT(po) as orderCount FROM ProductOrder po JOIN po.products p GROUP BY p ORDER BY orderCount DESC")
    List<Object[]> findTopOrderProducts(PageRequest pageable);
}
