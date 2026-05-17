package com.mycompany.rpg.exceptions;

public class EmptyInventoryException extends Exception {
    public EmptyInventoryException(String message) {
        super(message);
    }
}