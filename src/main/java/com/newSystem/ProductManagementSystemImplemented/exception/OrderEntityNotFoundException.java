package com.newSystem.ProductManagementSystemImplemented.exception;

public class OrderEntityNotFoundException extends RuntimeException{

    private final long serialVersionUID = 4;

    public OrderEntityNotFoundException(String message){
        super(message);
    }
}
