package org.fiap.test.spring.card.transaction.domain;

import org.assertj.core.api.Assertions;
import org.fiap.test.spring.card.limit.domain.LimitCard;
import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotEnoughForTransaction;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionCardServiceImplTest {

    private static final Student SAMPLE_STUDENT = new Student(1_000);
    private static final LimitCard SAMPLE_LIMIT_CARD = new LimitCard(new BigDecimal("125.56"), SAMPLE_STUDENT);

    @Mock
    private TransactionCardRepository transactionCardRepository;

    private TransactionCardService transactionCardService;

    @Before
    public void setUp() throws Exception {
        transactionCardService = new TransactionCardServiceImpl(transactionCardRepository);

        when(transactionCardRepository.balance(SAMPLE_STUDENT)).thenReturn(Optional.of(new BigDecimal("69.44")));
    }

    @Test(expected = LimitCardNotEnoughForTransaction.class)
    public void limit_doesnt_allow_new_transaction() throws LimitCardNotEnoughForTransaction {
        TransactionCard debitTransactionCard = TransactionCard.debit(
                new BigDecimal("65.36"),
                "buy t-shirt on shopping",
                SAMPLE_LIMIT_CARD
        );

        transactionCardService.validateCurrentLimitAllowsTransaction(SAMPLE_STUDENT, SAMPLE_LIMIT_CARD, debitTransactionCard);

        Assertions.failBecauseExceptionWasNotThrown(LimitCardNotEnoughForTransaction.class);
    }

    @Test
    public void limit_allows_new_transaction_so_no_exception_is_raised() throws LimitCardNotEnoughForTransaction {
        TransactionCard debitTransactionCard = TransactionCard.debit(
                new BigDecimal("55.00"),
                "buy t-shirt on shopping",
                SAMPLE_LIMIT_CARD
        );

        transactionCardService.validateCurrentLimitAllowsTransaction(SAMPLE_STUDENT, SAMPLE_LIMIT_CARD, debitTransactionCard);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void check_negative_minimum_transaction_value() throws InvalidSuppliedDataException {
        transactionCardService.validateTransactionMinimumValue(new BigDecimal("-1"));
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void check_null_minimum_transaction_value() throws InvalidSuppliedDataException {
        transactionCardService.validateTransactionMinimumValue(null);
    }

    @Test
    public void check_valid_positive_minimum_transaction_value() throws InvalidSuppliedDataException {
        transactionCardService.validateTransactionMinimumValue(new BigDecimal("1"));
    }
}