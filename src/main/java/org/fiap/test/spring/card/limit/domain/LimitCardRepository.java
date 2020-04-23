package org.fiap.test.spring.card.limit.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface LimitCardRepository extends JpaRepository<LimitCard, Integer> {

    @Query(value = "SELECT MAX( id ) FROM LimitCard WHERE student = :studentId")
    Integer maxIdByStudentId(Integer studentId);
}
