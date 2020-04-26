package org.fiap.test.spring.card.transaction.domain;

import org.fiap.test.spring.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

interface TransactionCardRepository extends JpaRepository<TransactionCard, Integer> {

    @Query(value = "SELECT SUM( td.value ) FROM TransactionCard td JOIN td.limitCard lc WHERE lc.student = :student")
    Optional<BigDecimal> balance(Student student);

    @Query(value = "SELECT td FROM TransactionCard td JOIN td.limitCard lc WHERE lc.student = :student AND td.uuid = :transactionUUIDToBeChargedBack")
    Optional<TransactionCard> retrieveTransactionCardToBeChargedBack(Student student, String transactionUUIDToBeChargedBack);
}
