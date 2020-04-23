package org.fiap.test.spring.card.limit.domain.usecase;

import org.fiap.test.spring.card.limit.domain.LimitCard;
import org.fiap.test.spring.card.limit.domain.LimitCardService;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;
import org.fiap.test.spring.student.domain.StudentId;
import org.fiap.test.spring.student.domain.StudentService;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class LimitCardUseCase {

    public static class StudentLimitCardPayload {
        BigDecimal value;

        public StudentLimitCardPayload() {
        }

        public StudentLimitCardPayload(BigDecimal value) {
            this.value = value;
        }

        public BigDecimal getValue() {
            return value;
        }
    }

    private final LimitCardService limitCardService;
    private final StudentService studentService;

    public LimitCardUseCase(LimitCardService limitCardService, StudentService studentService) {
        this.limitCardService = limitCardService;
        this.studentService = studentService;
    }

    @Transactional
    public void createNewLimitCard(String id, StudentLimitCardPayload studentLimitCardPayload) throws InvalidSuppliedDataException, StudentNotFoundException {
        StudentId studentId = studentService.parseStudentId(id);

        studentService.ensureStudentIsFound(studentId.getSubscription(), studentId.getCode());

        Student student = studentService.findStudent(studentId.getSubscription(), studentId.getCode());

        limitCardService.validateMinimumLimitValue(studentLimitCardPayload.getValue());

        this.limitCardService.insert(new LimitCard(studentLimitCardPayload.getValue(), student.getId()));
    }

    public StudentLimitCardPayload getCurrentCardLimit(String id) throws InvalidSuppliedDataException, StudentNotFoundException {
        StudentId studentId = studentService.parseStudentId(id);

        studentService.ensureStudentIsFound(studentId.getSubscription(), studentId.getCode());

        Student student = studentService.findStudent(studentId.getSubscription(), studentId.getCode());

        Optional<LimitCard> limitCard = limitCardService.getCurrentLimitCard(student.getId());

        return new StudentLimitCardPayload(limitCard.map(LimitCard::getValue).orElse(BigDecimal.ZERO));
    }
}
