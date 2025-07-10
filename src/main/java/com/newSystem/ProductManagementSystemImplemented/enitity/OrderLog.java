package com.newSystem.ProductManagementSystemImplemented.enitity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_log")
public class OrderLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false , fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Products  product;

    @ManyToOne(optional = false , fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(optional = false , fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id")
    private ProductOrder productOrder;

    @Column(name = "delivered_on")
    private Date deliveredOn;

    @Column(name = "product_inventory")
    private int productInventory;

    @Column(name = "total_order_price")
    private double totalOrderPrice;

}
