package com.newSystem.ProductManagementSystemImplemented.exception;

public class OrderWithIdNotFoundException extends RuntimeException{

    private final long serialversionUID = 5;

    public OrderWithIdNotFoundException(String message){
        super(message);
    }
}
