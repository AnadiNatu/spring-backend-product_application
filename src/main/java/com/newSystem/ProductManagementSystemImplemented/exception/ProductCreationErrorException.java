package com.newSystem.ProductManagementSystemImplemented.exception;

public class ProductCreationErrorException extends RuntimeException{

    private final long serialVersionUID = 1;

    public ProductCreationErrorException(String message){
        super(message);
    }
}
