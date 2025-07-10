package com.newSystem.ProductManagementSystemImplemented.exception;

public class ProductEntityNotFoundException extends RuntimeException{

    private final long serialVersionUID = 3;

    public ProductEntityNotFoundException(String message){
        super(message);
    }
}
