package org.fiap.test.spring.card.transaction.domain;

import org.fiap.test.spring.card.limit.domain.LimitCard;
import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotEnoughForTransaction;
import org.fiap.test.spring.card.transaction.domain.exception.TransactionCardToBeChargedBackNotFound;
import org.fiap.test.spring.card.transaction.domain.usecase.TransactionCardUseCase;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public interface TransactionCardService {

    void validateCurrentLimitAllowsTransaction(Student student, LimitCard currentLimitCard, TransactionCard transactionCard) throws LimitCardNotEnoughForTransaction;

    TransactionCard insert(TransactionCard debitTransactionCard);

    void validateTransactionMinimumValue(TransactionCardUseCase.TransactionCardPayload value) throws InvalidSuppliedDataException;

    String validateAndExtractTransactionUUID(TransactionCardUseCase.TransactionCardPayload transactionCardToBeChargedBackPayload) throws InvalidSuppliedDataException;

    TransactionCard retrieveTransactionCardToBeChargedBack(Student student, String transactionUUIDToBeChargedBack) throws TransactionCardToBeChargedBackNotFound;

    BigDecimal retrieveBalanceByMonth(Student student, YearMonth statementMonth);

    List<TransactionCard> retrieveStatementByMonth(Student student, YearMonth statementMonth);

    String formatStringStatementForAGivenMonth(YearMonth statementMonth, List<TransactionCardUseCase.TransactionCardPayload> transactions, BigDecimal currentLimitValue, BigDecimal monthBalance);
}
