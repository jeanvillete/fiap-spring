package org.fiap.test.spring.common.exception;

public class InvalidSuppliedDataException extends RuntimeException {
    public InvalidSuppliedDataException(String message) {
        super(message);
    }
}
