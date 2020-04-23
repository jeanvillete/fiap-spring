package org.fiap.test.spring.card.transaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;

interface TransactionRepository extends JpaRepository<Transaction , Integer> {
}
