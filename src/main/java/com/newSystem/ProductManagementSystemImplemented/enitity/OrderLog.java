package com.newSystem.ProductManagementSystemImplemented.enitity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

//    Can create a field for order name , combination of user name and product name;


}

















