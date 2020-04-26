package org.fiap.test.spring.card.transaction.domain.exception;

public class LimitCardNotEnoughForTransaction extends Exception {
    public LimitCardNotEnoughForTransaction(String message) {
        super(message);
    }
}
