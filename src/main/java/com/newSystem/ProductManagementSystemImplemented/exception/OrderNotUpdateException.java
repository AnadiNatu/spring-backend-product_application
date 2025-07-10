package com.newSystem.ProductManagementSystemImplemented.exception;

public class OrderNotUpdateException extends RuntimeException {
    private final long serialVersionUID = 8;
    public OrderNotUpdateException(String message) {
        super(message);
    }
}
