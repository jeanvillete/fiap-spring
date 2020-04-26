package org.fiap.test.spring.card.transaction.domain.usecase;

import org.fiap.test.spring.card.limit.domain.LimitCard;
import org.fiap.test.spring.card.limit.domain.LimitCardService;
import org.fiap.test.spring.card.transaction.domain.TransactionCard;
import org.fiap.test.spring.card.transaction.domain.TransactionCardService;
import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotEnoughForTransaction;
import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotFoundForATransaction;
import org.fiap.test.spring.card.transaction.domain.exception.TransactionCardToBeChargedBackNotFound;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;
import org.fiap.test.spring.student.domain.StudentId;
import org.fiap.test.spring.student.domain.StudentService;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionCardUseCase {

    public static class TransactionCardPayload {
        String uuid;
        BigDecimal value;
        String description;
        LocalDateTime date;

        TransactionCardPayload() {
        }

        TransactionCardPayload(BigDecimal value, String description) {
            this.value = value;
            this.description = description;
        }

        public TransactionCardPayload(BigDecimal value) {
            this.value = value;
        }

        public TransactionCardPayload(String uuid) {
            this.uuid = uuid;
        }

        public TransactionCardPayload(String uuid, BigDecimal value, String description) {
            this.uuid = uuid;
            this.value = value;
            this.description = description;
        }

        public TransactionCardPayload(TransactionCard transactionCard) {
            this.uuid = transactionCard.getUuid();
            this.value = transactionCard.getValue();
            this.description = transactionCard.getDescription();
            this.date = transactionCard.getDate();
        }

        public String getUuid() {
            return uuid;
        }

        public BigDecimal getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public LocalDateTime getDate() {
            return date;
        }
    }

    public static class TransactionCardStatementPayload {
        YearMonth statementMonth;
        BigDecimal currentLimitValue;
        BigDecimal monthBalance;
        List<TransactionCardPayload> transactions;

        public TransactionCardStatementPayload(YearMonth statementMonth) {
            this.statementMonth = statementMonth;
        }

        public TransactionCardStatementPayload(YearMonth statementMonth, BigDecimal currentLimitValue, BigDecimal monthBalance, List<TransactionCardPayload> transactions) {
            this.statementMonth = statementMonth;
            this.currentLimitValue = currentLimitValue;
            this.monthBalance = monthBalance;
            this.transactions = transactions;
        }

        public YearMonth getStatementMonth() {
            return statementMonth;
        }

        public BigDecimal getCurrentLimitValue() {
            return currentLimitValue;
        }

        public BigDecimal getMonthBalance() {
            return monthBalance;
        }

        public List<TransactionCardPayload> getTransactions() {
            return transactions;
        }
    }

    private final TransactionCardService transactionCardService;
    private final LimitCardService limitCardService;
    private final StudentService studentService;

    public TransactionCardUseCase(TransactionCardService transactionCardService, LimitCardService limitCardService, StudentService studentService) {
        this.transactionCardService = transactionCardService;
        this.limitCardService = limitCardService;
        this.studentService = studentService;
    }

    @Transactional
    public TransactionCardPayload debit(String id, TransactionCardPayload transactionCardPayload) throws InvalidSuppliedDataException, StudentNotFoundException, LimitCardNotFoundForATransaction, LimitCardNotEnoughForTransaction {
        StudentId studentId = studentService.parseStudentId(id);

        studentService.ensureStudentIsFound(studentId.getSubscription(), studentId.getCode());

        Student student = studentService.findStudent(studentId.getSubscription(), studentId.getCode());

        LimitCard currentLimitCard = limitCardService.getCurrentLimitCard(student)
                .orElseThrow(() -> new LimitCardNotFoundForATransaction("No limit is assigned to the student."));

        transactionCardService.validateTransactionMinimumValue(transactionCardPayload);

        TransactionCard debitTransactionCard = TransactionCard.debit(
                transactionCardPayload.getValue(),
                transactionCardPayload.getDescription(),
                currentLimitCard
        );

        transactionCardService.validateCurrentLimitAllowsTransaction(student, currentLimitCard, debitTransactionCard);

        TransactionCard createdTransactionCard = transactionCardService.insert(debitTransactionCard);

        return new TransactionCardPayload(createdTransactionCard.getUuid());
    }

    @Transactional
    public TransactionCardPayload chargeBack(String id, TransactionCardPayload transactionCardToBeChargedBackPayload) throws InvalidSuppliedDataException, StudentNotFoundException, TransactionCardToBeChargedBackNotFound, LimitCardNotFoundForATransaction {
        StudentId studentId = studentService.parseStudentId(id);

        studentService.ensureStudentIsFound(studentId.getSubscription(), studentId.getCode());

        Student student = studentService.findStudent(studentId.getSubscription(), studentId.getCode());

        LimitCard currentLimitCard = limitCardService.getCurrentLimitCard(student)
                .orElseThrow(() -> new LimitCardNotFoundForATransaction("No limit is assigned to the student."));

        String transactionUUIDToBeChargedBack = transactionCardService.validateAndExtractTransactionUUID(transactionCardToBeChargedBackPayload);

        TransactionCard transactionCardToBeChargedBack = transactionCardService.retrieveTransactionCardToBeChargedBack(student, transactionUUIDToBeChargedBack);

        TransactionCard transactionCardChargeBack = transactionCardService.insert(
                TransactionCard.chargeBack(
                        transactionCardToBeChargedBack.getValue().multiply(new BigDecimal(-1)),
                        String.format(
                                "Charging Back Transaction with value %s and identified by %s",
                                transactionCardToBeChargedBack.getValue().toString(),
                                transactionCardToBeChargedBack.getUuid()
                        ),
                        currentLimitCard
                )
        );

        return new TransactionCardPayload(
                transactionCardChargeBack.getUuid(),
                transactionCardChargeBack.getValue(),
                transactionCardChargeBack.getDescription()
        );
    }

    public TransactionCardPayload billPayment(String id, TransactionCardPayload transactionCardWithBillPaymentPayload) throws InvalidSuppliedDataException, StudentNotFoundException, LimitCardNotFoundForATransaction {
        StudentId studentId = studentService.parseStudentId(id);

        studentService.ensureStudentIsFound(studentId.getSubscription(), studentId.getCode());

        Student student = studentService.findStudent(studentId.getSubscription(), studentId.getCode());

        LimitCard currentLimitCard = limitCardService.getCurrentLimitCard(student)
                .orElseThrow(() -> new LimitCardNotFoundForATransaction("No limit is assigned to the student."));

        transactionCardService.validateTransactionMinimumValue(transactionCardWithBillPaymentPayload);

        TransactionCard transactionCardBillPayment = transactionCardService.insert(
                TransactionCard.billPayment(
                        transactionCardWithBillPaymentPayload.getValue().multiply(new BigDecimal(-1)),
                        String.format(
                                "Payment bill with value %s",
                                transactionCardWithBillPaymentPayload.getValue().toString()
                        ),
                        currentLimitCard
                )
        );

        return new TransactionCardPayload(
                transactionCardBillPayment.getUuid(),
                transactionCardBillPayment.getValue(),
                transactionCardBillPayment.getDescription()
        );
    }

    public TransactionCardStatementPayload loadStatementForAGivenMonth(String id, YearMonth statementMonth) throws InvalidSuppliedDataException, StudentNotFoundException, LimitCardNotFoundForATransaction {
        StudentId studentId = studentService.parseStudentId(id);

        studentService.ensureStudentIsFound(studentId.getSubscription(), studentId.getCode());

        Student student = studentService.findStudent(studentId.getSubscription(), studentId.getCode());

        BigDecimal currentLimitCard = limitCardService.getCurrentLimitCard(student)
                .map(LimitCard::getValue)
                .orElse(BigDecimal.ZERO);

        BigDecimal monthBalance = transactionCardService.retrieveBalanceByMonth(student, statementMonth);

        List<TransactionCard> transactions = transactionCardService.retrieveStatementByMonth(student, statementMonth);

        List<TransactionCardPayload> transactionCardPayloadListForStatement = transactions.stream()
                .map(TransactionCardPayload::new)
                .collect(Collectors.toList());

        return new TransactionCardStatementPayload(
                statementMonth,
                currentLimitCard,
                monthBalance,
                transactionCardPayloadListForStatement
        );
    }

    public String loadFormattedStringStatementForAGivenMonth(String id, YearMonth statementMonth) throws InvalidSuppliedDataException, StudentNotFoundException, LimitCardNotFoundForATransaction {
        TransactionCardStatementPayload loadStatementForAGivenMonth = loadStatementForAGivenMonth(id, statementMonth);

        return transactionCardService.formatStringStatementForAGivenMonth(
                loadStatementForAGivenMonth.getStatementMonth(),
                loadStatementForAGivenMonth.getTransactions(),
                loadStatementForAGivenMonth.getCurrentLimitValue(),
                loadStatementForAGivenMonth.getMonthBalance()
        );
    }

}
