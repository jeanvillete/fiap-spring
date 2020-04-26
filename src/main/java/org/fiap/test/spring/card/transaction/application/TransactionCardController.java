package org.fiap.test.spring.card.transaction.application;

import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotEnoughForTransaction;
import org.fiap.test.spring.card.transaction.domain.exception.LimitCardNotFoundForATransaction;
import org.fiap.test.spring.card.transaction.domain.exception.TransactionCardToBeChargedBackNotFound;
import org.fiap.test.spring.card.transaction.domain.usecase.TransactionCardUseCase;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("students/{id}/card")
public class TransactionCardController {

    private final TransactionCardUseCase transactionCardUseCase;

    public TransactionCardController(TransactionCardUseCase transactionCardUseCase) {
        this.transactionCardUseCase = transactionCardUseCase;
    }

    @PostMapping("debit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionCardUseCase.TransactionCardPayload createDebitTransaction(
            @PathVariable String id,
            @RequestBody TransactionCardUseCase.TransactionCardPayload debitTransactionCard
    ) throws
            InvalidSuppliedDataException,
            LimitCardNotFoundForATransaction,
            StudentNotFoundException,
            LimitCardNotEnoughForTransaction {

        return transactionCardUseCase.debit(id, debitTransactionCard);
    }

    @PostMapping("charge-back/{transactionUUIDToBeChargedBack}")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionCardUseCase.TransactionCardPayload createChargeBackTransaction(
            @PathVariable String id,
            @PathVariable String transactionUUIDToBeChargedBack
    ) throws
            InvalidSuppliedDataException,
            StudentNotFoundException,
            TransactionCardToBeChargedBackNotFound,
            LimitCardNotFoundForATransaction {

        return transactionCardUseCase.chargeBack(
                id,
                new TransactionCardUseCase.TransactionCardPayload(
                        transactionUUIDToBeChargedBack
                )
        );
    }

    @PostMapping("bill-payment")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionCardUseCase.TransactionCardPayload createBillPaymentTransaction(
            @PathVariable String id,
            @RequestBody TransactionCardUseCase.TransactionCardPayload paymentBillTransactionCard
    ) throws
            InvalidSuppliedDataException,
            StudentNotFoundException,
            LimitCardNotFoundForATransaction {

        return transactionCardUseCase.billPayment(id, paymentBillTransactionCard);
    }

}
