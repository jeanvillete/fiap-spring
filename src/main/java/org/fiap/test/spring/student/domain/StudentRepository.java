package org.fiap.test.spring.student.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface StudentRepository extends JpaRepository<Student, Integer> {

    Integer countByName(String name);

    @Query(value = "SELECT MAX(subscription) FROM Student")
    Integer highestSubscriptionValue();

}
