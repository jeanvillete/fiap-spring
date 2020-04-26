package org.fiap.test.spring.card.transaction.domain.exception;

public class TransactionCardToBeChargedBackNotFound extends Exception {
    public TransactionCardToBeChargedBackNotFound(String message) {
        super(message);
    }
}
