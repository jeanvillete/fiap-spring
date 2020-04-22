package org.fiap.test.spring.student.domain.exception;

public class StudentNameConflictException extends Exception {
    public StudentNameConflictException(String message) {
        super(message);
    }
}
