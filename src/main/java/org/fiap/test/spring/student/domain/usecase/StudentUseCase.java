package org.fiap.test.spring.student.domain.usecase;

import org.fiap.test.spring.student.domain.StudentService;
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

    public static class StudentCreated {
        String id;
        String name;

        public StudentCreated(String id, String name) {
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

    public StudentBatchParsedContent parseBatchContent(String fileLineContent) {
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
    public StudentCreated createStudent(String name) {
        String normalizedName = studentService.nameNormalization(name);

        studentService.validateName(normalizedName);

        studentService.checkForConflictOnInsert(normalizedName);

        Integer highestCurrentSubscription = studentService.highestSubscriptionValue();
        Integer newSubscription = highestCurrentSubscription + 1;

        Integer newCode = studentService.randomCodeValue();

        studentService.insert(normalizedName, newSubscription, newCode);

        String identification = studentService.formatIdentification(newSubscription, newCode);

        return new StudentCreated(
                identification,
                normalizedName
        );
    }
}
