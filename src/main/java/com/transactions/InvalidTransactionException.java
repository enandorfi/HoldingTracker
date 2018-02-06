package com.transactions;

/**
 * Checked exception class, thrown by methods in Transaction class if a Transaction with invalid parameters is created
 */

public class InvalidTransactionException extends Exception {
    InvalidTransactionException(String message){
        super(message);
    }
}
