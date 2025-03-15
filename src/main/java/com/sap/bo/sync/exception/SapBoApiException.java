package com.sap.bo.sync.exception;

/**
 * Exception thrown when there's an error with the SAP BO API
 */
public class SapBoApiException extends RuntimeException {
    
    public SapBoApiException(String message) {
        super(message);
    }
    
    public SapBoApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
