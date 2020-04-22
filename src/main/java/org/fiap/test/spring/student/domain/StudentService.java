package org.fiap.test.spring.student.domain;

import org.fiap.test.spring.student.domain.usecase.StudentUseCase;

public interface StudentService {
    void validateFileLineContent(String fileLineContent);

    String parseStudentName(String fileLineContent);

    Integer parseStudentSubscription(String fileLineContent);

    Integer parseStudentCode(String fileLineContent);

    void insert(StudentUseCase.StudentBatchParsedContent studentBatchParsedContent);

    String nameNormalization(String name);

    void validateName(String name);

    void checkForConflictOnInsert(String name);

    Integer highestSubscriptionValue();

    Integer randomCodeValue();

    void insert(String name, Integer subscription, Integer code);

    String formatIdentification(Integer subscription, Integer code);
}
