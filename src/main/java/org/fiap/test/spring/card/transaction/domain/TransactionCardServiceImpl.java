package org.fiap.test.spring.card.transaction.domain;

import org.fiap.test.spring.card.limit.domain.LimitCard;
import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotEnoughForTransaction;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
class TransactionCardServiceImpl implements TransactionCardService {

    private final TransactionCardRepository transactionCardRepository;

    TransactionCardServiceImpl(TransactionCardRepository transactionCardRepository) {
        this.transactionCardRepository = transactionCardRepository;
    }

    @Override
    public void validateCurrentLimitAllowsTransaction(Student student, LimitCard currentLimitCard, TransactionCard transactionCard) throws LimitCardNotEnoughForTransaction {
        Optional<BigDecimal> studentCurrentBalance = transactionCardRepository.balance(student);

        BigDecimal balanceAfterTransaction = studentCurrentBalance
                .orElse(BigDecimal.ZERO)
                .add(transactionCard.getValue());

        if (currentLimitCard.getValue().compareTo(balanceAfterTransaction) == -1) {
            throw new LimitCardNotEnoughForTransaction("The current limit isn't enough to allow this transaction.");
        }
    }

    @Override
    public TransactionCard insert(final TransactionCard debitTransactionCard) {
        transactionCardRepository.save(debitTransactionCard.generateUUID());

        return debitTransactionCard;
    }

    @Override
    public void validateTransactionMinimumValue(BigDecimal value) throws InvalidSuppliedDataException {
        if (value == null) {
            throw new InvalidSuppliedDataException("Argument value cannot be null");
        }

        if (value.compareTo(BigDecimal.ZERO) != 1) {
            throw new InvalidSuppliedDataException("Argument value must be greater than zero (0)");
        }
    }

}
