package org.fiap.test.spring.card.limit.domain.usecase;

import org.fiap.test.spring.card.limit.domain.Limit;
import org.fiap.test.spring.card.limit.domain.LimitService;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;
import org.fiap.test.spring.student.domain.StudentId;
import org.fiap.test.spring.student.domain.StudentService;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class LimitUseCase {

    public static class StudentLimitPayload {
        BigDecimal value;

        public BigDecimal getValue() {
            return value;
        }
    }

    private final LimitService limitService;
    private final StudentService studentService;

    public LimitUseCase(LimitService limitService, StudentService studentService) {
        this.limitService = limitService;
        this.studentService = studentService;
    }

    @Transactional
    public void createNewCardLimit(String id, StudentLimitPayload studentLimitPayload) throws InvalidSuppliedDataException, StudentNotFoundException {
        StudentId studentId = studentService.parseStudentId(id);

        studentService.ensureStudentIsFound(studentId.getSubscription(), studentId.getCode());

        Student student = studentService.findStudent(studentId.getSubscription(), studentId.getCode());

        limitService.validateMinimumLimitValue(studentLimitPayload.getValue());

        this.limitService.insert(new Limit(studentLimitPayload.getValue(), student.getId()));
    }
}
