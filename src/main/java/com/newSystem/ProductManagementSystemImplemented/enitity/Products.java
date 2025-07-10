package com.newSystem.ProductManagementSystemImplemented.enitity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "product")
@EqualsAndHashCode(exclude = {"productOrders"})
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    public Long id;

    @Column(name = "product_name" , nullable = false)
    public String productName;

    @Column(name = "product_desc" , length = 1000)
    public String productDesc;

    @Column(name = "inventory" , nullable = false)
    public int productInventory;


    @Column(nullable = false)
    public double price ;

    @Builder.Default
    @OneToMany(mappedBy = "products" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<ProductOrder> productOrders = new ArrayList<>();

    @Override
    public String toString() {
        return "Products{" +
                "productId=" + id +
                ", productName='" + productName + '\'' +
                ", productDesc='" + productDesc + '\'' +
                ", productInventory" + productInventory + '\'' +
                ", price=" + price +
                '}';
    }
}
