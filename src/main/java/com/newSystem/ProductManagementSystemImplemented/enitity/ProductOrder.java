package com.newSystem.ProductManagementSystemImplemented.enitity;

import com.newSystem.ProductManagementSystemImplemented.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product_orders")
@EqualsAndHashCode(exclude = {"users" , "products"})
public class ProductOrder {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_date" , nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "estimate_delivery_date")
    private LocalDateTime estimateDeliveryDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "order_quantity" , nullable = false)
    private int orderQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "late_delivery_status")
    private boolean lateDeliveryStatus;

    @Column(name = "order_price" ,nullable = false)
    private double orderPrice;

    @ManyToOne(optional = false , fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private Users users;

    @ManyToOne(optional = false , fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id" , nullable = false)
    private Products products;

    @Override
    public String toString() {
        return "ProductOrder{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", orderQuantity=" + orderQuantity +
                ", users=" + (users != null ? users.getId() : null) +  // Optionally include user's ID
                ", products=" + (products != null ? products.getId() : null) +  // Optionally include product's ID
                '}';
    }

}
