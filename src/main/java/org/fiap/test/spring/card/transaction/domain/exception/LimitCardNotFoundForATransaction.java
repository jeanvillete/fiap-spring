package org.fiap.test.spring.card.transaction.domain.exception;

public class LimitCardNotFoundForATransaction extends Exception {
    public LimitCardNotFoundForATransaction(String message) {
        super(message);
    }
}
