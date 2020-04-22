package org.fiap.test.spring.student.domain.exception;

public class StudentNameConflictException extends RuntimeException {
    public StudentNameConflictException(String message) {
        super(message);
    }
}
