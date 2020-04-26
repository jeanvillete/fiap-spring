package org.fiap.test.spring.card.transaction.domain;

import org.codehaus.plexus.util.StringUtils;
import org.fiap.test.spring.card.limit.domain.LimitCard;
import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotEnoughForTransaction;
import org.fiap.test.spring.card.transaction.domain.exception.TransactionCardToBeChargedBackNotFound;
import org.fiap.test.spring.card.transaction.domain.usecase.TransactionCardUseCase;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    public void validateTransactionMinimumValue(TransactionCardUseCase.TransactionCardPayload transactionCardPayload) throws InvalidSuppliedDataException {
        Optional.ofNullable(transactionCardPayload)
                .map(TransactionCardUseCase.TransactionCardPayload::getValue)
                .filter(Objects::nonNull)
                .filter(value -> value.compareTo(BigDecimal.ZERO) == 1)
                .orElseThrow(() ->
                        new InvalidSuppliedDataException("Argument value must be supplied and greater than zero (0)")
                );
    }

    @Override
    public String validateAndExtractTransactionUUID(TransactionCardUseCase.TransactionCardPayload transactionCardToBeChargedBackPayload) throws InvalidSuppliedDataException {
        return Optional.ofNullable(transactionCardToBeChargedBackPayload)
                .map(TransactionCardUseCase.TransactionCardPayload::getUuid)
                .filter(Objects::nonNull)
                .filter(StringUtils::isNotBlank)
                .filter(uuid -> uuid.length() == 15)
                .orElseThrow(() -> new InvalidSuppliedDataException("Argument uuid is mandatory, cannot be neither null nor empty/blank."));
    }

    @Override
    public TransactionCard retrieveTransactionCardToBeChargedBack(Student student, String transactionUUIDToBeChargedBack) throws TransactionCardToBeChargedBackNotFound {
        return transactionCardRepository.retrieveTransactionCardToBeChargedBack(student, transactionUUIDToBeChargedBack)
                .orElseThrow(() ->
                        new TransactionCardToBeChargedBackNotFound(
                                "No transaction card was found for the provided student id and transaction uuid."
                        )
                );
    }

    private LocalDateTime veryLastTimeForStatementMonth(YearMonth statementMonth) {
        return LocalDateTime.of(statementMonth.atEndOfMonth(), LocalTime.MAX);
    }

    private LocalDateTime veryFirstTimeWithinForStatementMonth(YearMonth statementMonth) {
        return LocalDateTime.of(statementMonth.atDay(1), LocalTime.MIN);
    }

    @Override
    public BigDecimal retrieveBalanceByMonth(Student student, YearMonth statementMonth) {
        LocalDateTime veryLastTimeForStatementMonth = veryLastTimeForStatementMonth(statementMonth);

        return transactionCardRepository.balance(student, veryLastTimeForStatementMonth)
            .orElse(BigDecimal.ZERO);
    }

    @Override
    public List<TransactionCard> retrieveStatementByMonth(Student student, YearMonth statementMonth) {
        LocalDateTime firstTimeWithinForStatementMonth = veryFirstTimeWithinForStatementMonth(statementMonth);
        LocalDateTime lastTimeForStatementMonth = veryLastTimeForStatementMonth(statementMonth);

        return transactionCardRepository.findByStudentAndDateQuery(student, firstTimeWithinForStatementMonth, lastTimeForStatementMonth)
                .orElse(Collections.EMPTY_LIST);
    }

    @Override
    public String formatStringStatementForAGivenMonth(YearMonth statementMonth, List<TransactionCardUseCase.TransactionCardPayload> transactions, BigDecimal currentLimitValue, BigDecimal monthBalance) {
        StringBuilder content = new StringBuilder();

        content.append("\n");
        content.append("statement year month     " + statementMonth);
        content.append("\n");

        transactions
                .forEach(transactionCardPayload -> {
                    content.append("\n");
                    content.append(transactionCardPayload.getDate() + " " + transactionCardPayload.getUuid());
                    content.append("\n");
                    content.append("value                    " + transactionCardPayload.getValue());
                    content.append("\n");
                    content.append("description              " + Optional.ofNullable(transactionCardPayload.getDescription()).orElse("-"));
                    content.append("\n");
                });

        content.append("\n");
        content.append("current limit            " + currentLimitValue);
        content.append("\n");

        content.append("\n");
        content.append("month balance            " + monthBalance);

        return content.toString();
    }

}
