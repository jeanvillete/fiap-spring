package org.fiap.test.spring.student.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface StudentRepository extends JpaRepository<Student, Integer> {

    Optional<Integer> countByName(String name);

    @Query(value = "SELECT MAX(subscription) FROM Student")
    Optional<Integer> highestSubscriptionValue();

    Optional<Integer> countBySubscriptionAndCode(Integer subscription, Integer code);

    Optional<Integer> countBySubscriptionNotAndCodeNotAndName(Integer subscription, Integer code, String name);

    Optional<Student> findBySubscriptionAndCode(Integer subscription, Integer code);

    Optional<List<Student>> findAllByNameContaining(String name);
}
