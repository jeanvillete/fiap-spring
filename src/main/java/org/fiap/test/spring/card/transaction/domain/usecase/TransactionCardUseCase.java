package org.fiap.test.spring.card.transaction.domain.usecase;

import org.fiap.test.spring.card.limit.domain.LimitCard;
import org.fiap.test.spring.card.limit.domain.LimitCardService;
import org.fiap.test.spring.card.transaction.domain.TransactionCard;
import org.fiap.test.spring.card.transaction.domain.TransactionCardService;
import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotEnoughForTransaction;
import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotFoundForATransaction;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;
import org.fiap.test.spring.student.domain.StudentId;
import org.fiap.test.spring.student.domain.StudentService;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class TransactionCardUseCase {

    public static class TransactionCardPayload {
        String uuid;
        BigDecimal value;
        String description;

        TransactionCardPayload() {
        }

        TransactionCardPayload(BigDecimal value, String description) {
            this.value = value;
            this.description = description;
        }

        TransactionCardPayload(String uuid) {
            this.uuid = uuid;
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

        transactionCardService.validateTransactionMinimumValue(transactionCardPayload.getValue());

        TransactionCard debitTransactionCard = TransactionCard.debit(
                transactionCardPayload.getValue(),
                transactionCardPayload.getDescription(),
                currentLimitCard
        );

        transactionCardService.validateCurrentLimitAllowsTransaction(student, currentLimitCard, debitTransactionCard);

        TransactionCard createdTransactionCard = transactionCardService.insert(debitTransactionCard);

        return new TransactionCardPayload(createdTransactionCard.getUuid());
    }

}
