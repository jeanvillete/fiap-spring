package org.fiap.test.spring.student.domain;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.application.StudentActuator;
import org.fiap.test.spring.student.domain.exception.StudentNameConflictException;
import org.fiap.test.spring.student.domain.usecase.StudentUseCase;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.regex.Pattern;

@Service
class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentActuator studentActuator;

    public StudentServiceImpl(StudentRepository studentRepository, StudentActuator studentActuator) {
        this.studentRepository = studentRepository;
        this.studentActuator = studentActuator;
    }

    @Override
    public void validateFileLineContent(String fileLineContent) {
        if (fileLineContent == null || fileLineContent.isEmpty()) {
            throw new InvalidSuppliedDataException("Line content is null or empty.");
        }
        if (fileLineContent.length() != 55) {
            throw new InvalidSuppliedDataException("Line content is not 55 length.");
        }

        String name = fileLineContent.substring(0, 41).trim();
        if (!Pattern.compile("[\\w\\s]+").matcher(name).matches()) {
            throw new InvalidSuppliedDataException("There's no name value on the argument line content.");
        }
    }

    @Override
    public String parseStudentName(String fileLineContent) {
        return fileLineContent.substring(0, 41).trim();
    }

    @Override
    public Integer parseStudentSubscription(String fileLineContent) {
        return Integer.valueOf(fileLineContent.substring(41, 48));
    }

    @Override
    public Integer parseStudentCode(String fileLineContent) {
        return Integer.valueOf(fileLineContent.substring(49, 55).replaceAll("-", ""));
    }

    @Override
    public void insert(StudentUseCase.StudentBatchParsedContent studentBatchParsedContent) {
        this.insert(
                new Student(
                        studentBatchParsedContent.getName(),
                        studentBatchParsedContent.getSubscription(),
                        studentBatchParsedContent.getCode()
                )
        );
    }

    @Override
    public void insert(String name, Integer subscription, Integer code) {
        this.insert(
                new Student(
                        name,
                        subscription,
                        code
                )
        );
    }

    @Override
    public String formatIdentification(Integer subscription, Integer code) {
        String codeBeforeDash = String.valueOf(code).substring(0, 3);
        String codeAfterDash = String.valueOf(code).substring(3, 5);

        return String.format("%d %s-%s", subscription, codeBeforeDash, codeAfterDash);
    }

    @Override
    public String nameNormalization(String name) {
        if (name == null) {
            throw new InvalidSuppliedDataException("Argument name cannot be null.");
        }
        return name.trim().toUpperCase();
    }

    @Override
    public void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new InvalidSuppliedDataException("Argument name cannot be neither null nor empty.");
        }

        if (!Pattern.compile("[A-Z]{3,}(\\s[A-Z]{3,})+").matcher(name).matches()) {
            throw new InvalidSuppliedDataException("Invalid content provided on argument name.");
        }
    }

    @Override
    public void checkForConflictOnInsert(String name) {
        Integer countByName = studentRepository.countByName(name);

        if (countByName > 0) {
            throw new StudentNameConflictException("Student with name " + name + " is already recorded.");
        }
    }

    @Override
    public Integer highestSubscriptionValue() {
        return studentRepository.highestSubscriptionValue();
    }

    @Override
    public Integer randomCodeValue() {
        return new Random().ints(10_000, 99_999 + 1).findFirst().getAsInt();
    }

    private void insert(Student student) {
        this.studentRepository.save(student);
        this.studentActuator.increment();
    }
}
