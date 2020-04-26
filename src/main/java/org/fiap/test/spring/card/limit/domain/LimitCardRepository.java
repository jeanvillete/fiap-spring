package org.fiap.test.spring.card.limit.domain;

import org.fiap.test.spring.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface LimitCardRepository extends JpaRepository<LimitCard, Integer> {

    @Query(value = "SELECT MAX( id ) FROM LimitCard WHERE student = :student")
    Optional<Integer> maxIdByStudentId(Student student);
}
