package org.fiap.test.spring.card.transaction.domain;

import org.fiap.test.spring.card.limit.domain.LimitCard;
import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotEnoughForTransaction;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;

import java.math.BigDecimal;

public interface TransactionCardService {

    void validateCurrentLimitAllowsTransaction(Student student, LimitCard currentLimitCard, TransactionCard transactionCard) throws LimitCardNotEnoughForTransaction;

    TransactionCard insert(TransactionCard debitTransactionCard);

    void validateTransactionMinimumValue(BigDecimal value) throws InvalidSuppliedDataException;
}
