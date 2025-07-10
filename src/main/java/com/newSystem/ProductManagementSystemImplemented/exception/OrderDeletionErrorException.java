package com.newSystem.ProductManagementSystemImplemented.exception;

public class OrderDeletionErrorException extends Throwable {

    private final long serialVersionUID = 9;

    public OrderDeletionErrorException(String message) {
        super(message);
    }
}
