package org.fiap.test.spring.student.domain;

import org.springframework.data.jpa.repository.JpaRepository;

interface StudentRepository extends JpaRepository<Student, Integer> {
}
