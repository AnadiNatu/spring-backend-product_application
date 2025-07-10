package com.newSystem.ProductManagementSystemImplemented.exception;

public class OrderByUserForProductException extends RuntimeException{

    private final long serialVersionUID = 6;

    public OrderByUserForProductException(String message){
        super(message);
    }
}
