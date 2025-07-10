package com.newSystem.ProductManagementSystemImplemented.respository;


import com.newSystem.ProductManagementSystemImplemented.enitity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {

    Optional<Products> findByProductNameIgnoreCase(String productName);

    @Query("SELECT p FROM Products p WHERE p.price >= :lowerLimit AND p.price <= :upperLimit")
    List<Products> getAllProductsBetweenPriceLimit(@Param("lowerLimit") int lowerLimit , @Param("upperLimit") int upperLimit);

    boolean existsByProductNameIgnoreCase(String productName);

    @Query("SELECT p FROM Products p ORDER BY p.price ASC")
    List<Products> findAllByOrderByPriceAsc();

    @Query("SELECT p FROM Products p ORDER BY p.price DESC")
    List<Products> findAllByOrderByPriceDesc();

}
