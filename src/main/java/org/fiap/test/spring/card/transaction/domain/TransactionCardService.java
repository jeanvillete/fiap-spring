package org.fiap.test.spring.card.transaction.domain;

import org.fiap.test.spring.card.limit.domain.LimitCard;
import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotEnoughForTransaction;
import org.fiap.test.spring.card.transaction.domain.exception.TransactionCardToBeChargedBackNotFound;
import org.fiap.test.spring.card.transaction.domain.usecase.TransactionCardUseCase;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;

public interface TransactionCardService {

    void validateCurrentLimitAllowsTransaction(Student student, LimitCard currentLimitCard, TransactionCard transactionCard) throws LimitCardNotEnoughForTransaction;

    TransactionCard insert(TransactionCard debitTransactionCard);

    void validateTransactionMinimumValue(TransactionCardUseCase.TransactionCardPayload value) throws InvalidSuppliedDataException;

    String validateAndExtractTransactionUUID(TransactionCardUseCase.TransactionCardPayload transactionCardToBeChargedBackPayload) throws InvalidSuppliedDataException;

    TransactionCard retrieveTransactionCardToBeChargedBack(Student student, String transactionUUIDToBeChargedBack) throws TransactionCardToBeChargedBackNotFound;
}
