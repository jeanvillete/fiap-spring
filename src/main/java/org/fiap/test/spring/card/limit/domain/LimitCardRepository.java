package org.fiap.test.spring.card.limit.domain;

import org.fiap.test.spring.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface LimitCardRepository extends JpaRepository<LimitCard, Integer> {

    @Query(value = "SELECT MAX( id ) FROM LimitCard WHERE student = :student")
    Integer maxIdByStudentId(Student student);
}
