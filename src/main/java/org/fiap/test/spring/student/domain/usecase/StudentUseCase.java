package org.fiap.test.spring.student.domain.usecase;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.StudentService;
import org.fiap.test.spring.student.domain.exception.StudentNameConflictException;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StudentUseCase {

    public static class StudentBatchParsedContent {
        String name;
        Integer subscription;
        Integer code;

        public StudentBatchParsedContent(String name, Integer subscription, Integer code) {
            this.name = name;
            this.subscription = subscription;
            this.code = code;
        }

        @Override
        public String toString() {
            return "StudentBatchParsedContent{" +
                    "name='" + name + '\'' +
                    ", subscription=" + subscription +
                    ", code=" + code +
                    '}';
        }

        public String getName() {
            return name;
        }

        public Integer getSubscription() {
            return subscription;
        }

        public Integer getCode() {
            return code;
        }
    }

    public static class StudentId {
        Integer subscription;
        Integer code;

        public StudentId(Integer subscription, Integer code) {
            this.subscription = subscription;
            this.code = code;
        }

        public Integer getSubscription() {
            return subscription;
        }

        public Integer getCode() {
            return code;
        }
    }

    public static class StudentPayload {
        String id;
        String name;

        public StudentPayload(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private final StudentService studentService;

    public StudentUseCase(StudentService studentService) {
        this.studentService = studentService;
    }

    public StudentBatchParsedContent parseBatchContent(String fileLineContent) throws InvalidSuppliedDataException {
        this.studentService.validateFileLineContent(fileLineContent);

        String name = this.studentService.parseStudentName(fileLineContent);
        Integer subscription = this.studentService.parseStudentSubscription(fileLineContent);
        Integer code = this.studentService.parseStudentCode(fileLineContent);

        return new StudentBatchParsedContent(name, subscription, code);
    }

    public void insertStudent(StudentBatchParsedContent studentBatchParsedContent) {
        this.studentService.insert(studentBatchParsedContent);
    }

    @Transactional
    public StudentPayload createStudent(String name) throws StudentNameConflictException, InvalidSuppliedDataException {
        String normalizedName = studentService.nameNormalization(name);

        studentService.validateName(normalizedName);

        studentService.checkForConflictOnInsert(normalizedName);

        Integer highestCurrentSubscription = studentService.highestSubscriptionValue();
        Integer newSubscription = highestCurrentSubscription + 1;

        Integer newCode = studentService.randomCodeValue();

        studentService.insert(normalizedName, newSubscription, newCode);

        String identification = studentService.formatIdentification(newSubscription, newCode);

        return new StudentPayload(
                identification,
                normalizedName
        );
    }

    @Transactional
    public void updateStudentName(String studentId, StudentPayload studentPayload) throws StudentNotFoundException, StudentNameConflictException, InvalidSuppliedDataException {
        String normalizedName = studentService.nameNormalization(studentPayload.getName());

        StudentId parsedStudentId = studentService.parseStudentId(studentId);
        studentService.validateName(normalizedName);

        studentService.ensureStudentIsFound(parsedStudentId.getSubscription(), parsedStudentId.getCode());
        studentService.checkForConflictOnUpdate(parsedStudentId.getSubscription(), parsedStudentId.getCode(), normalizedName);

        studentService.updateName(parsedStudentId.getSubscription(), parsedStudentId.getCode(), normalizedName);
    }
}
