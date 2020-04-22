package org.fiap.test.spring.student.domain;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.exception.StudentNameConflictException;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.fiap.test.spring.student.domain.usecase.StudentUseCase;

public interface StudentService {

    void validateFileLineContent(String fileLineContent) throws InvalidSuppliedDataException;

    String parseStudentName(String fileLineContent);

    Integer parseStudentSubscription(String fileLineContent);

    Integer parseStudentCode(String fileLineContent);

    void insert(StudentUseCase.StudentBatchParsedContent studentBatchParsedContent);

    String nameNormalization(String name) throws InvalidSuppliedDataException;

    void validateName(String name) throws InvalidSuppliedDataException;

    void checkForConflictOnInsert(String name) throws StudentNameConflictException;

    Integer highestSubscriptionValue();

    Integer randomCodeValue();

    void insert(String name, Integer subscription, Integer code);

    String formatIdentification(Integer subscription, Integer code);

    StudentUseCase.StudentId parseStudentId(String studentId) throws InvalidSuppliedDataException;

    void ensureStudentIsFound(Integer subscription, Integer code) throws StudentNotFoundException;

    void checkForConflictOnUpdate(Integer subscription, Integer code, String name) throws StudentNameConflictException;

    void updateName(Integer subscription, Integer code, String name);
}
