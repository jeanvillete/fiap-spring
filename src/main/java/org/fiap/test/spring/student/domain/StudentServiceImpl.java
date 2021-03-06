package org.fiap.test.spring.student.domain;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.application.StudentActuator;
import org.fiap.test.spring.student.domain.exception.StudentNameConflictException;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.fiap.test.spring.student.domain.usecase.StudentUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentActuator studentActuator;

    StudentServiceImpl(StudentRepository studentRepository, StudentActuator studentActuator) {
        this.studentRepository = studentRepository;
        this.studentActuator = studentActuator;
    }

    @Override
    public void validateFileLineContent(String fileLineContent) throws InvalidSuppliedDataException {
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
    @Transactional
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
    @Transactional
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
    public StudentId parseStudentId(String studentId) throws InvalidSuppliedDataException {
        if (studentId == null || studentId.isEmpty()) {
            throw new InvalidSuppliedDataException("Parameter student id is mandatory.");
        }

        Matcher studentIdMatcher = Pattern.compile("(\\d{7})\\s(\\d{3})-(\\d{2})").matcher(studentId);
        if (!studentIdMatcher.matches()) {
            throw new InvalidSuppliedDataException("Invalid content provided on argument student id, it must follow the format '####### ###-##'.");
        }

        Integer studentSubscription = Integer.valueOf(studentIdMatcher.group(1));

        String codeBeforeDash = studentIdMatcher.group(2);
        String codeAfterDash = studentIdMatcher.group(3);
        Integer studentCode = Integer.valueOf(codeBeforeDash + codeAfterDash);

        return new StudentId(
                studentSubscription,
                studentCode
        );
    }

    @Override
    public void ensureStudentIsFound(Integer subscription, Integer code) throws StudentNotFoundException {
        studentRepository.countBySubscriptionAndCode(subscription, code)
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Student not found for the provided id; " +
                                        formatIdentification(
                                                subscription,
                                                code
                                        )
                        )
                );
    }

    @Override
    public void checkForConflictOnUpdate(Integer subscription, Integer code, String name) throws StudentNameConflictException {
        Integer count = studentRepository.countBySubscriptionNotAndCodeNotAndName(subscription, code, name).orElse(0);

        if (count > 0) {
            throw new StudentNameConflictException(
                    "It is not possible update student's name cause the requested name is already being used."
            );
        }
    }

    @Override
    @Transactional
    public void updateName(Integer subscription, Integer code, String name) {
        Student currentStudent = studentRepository.findBySubscriptionAndCode(subscription, code).get();

        currentStudent.setName(name);

        studentRepository.save(currentStudent);
    }

    @Override
    public List<StudentUseCase.StudentPayload> searchByName(String nameLikeClause) throws InvalidSuppliedDataException {
        if (nameLikeClause == null || nameLikeClause.isEmpty()) {
            throw new InvalidSuppliedDataException("Argument name for search cannot be null.");
        }
        if (nameLikeClause.length() < 3) {
            throw new InvalidSuppliedDataException("Argument name for search must be greater than 3 characters.");
        }

        return studentRepository.findAllByNameContaining(nameLikeClause)
                .orElse(Collections.emptyList())
                .stream()
                .map(student ->
                        new StudentUseCase.StudentPayload(
                                formatIdentification(
                                        student.getSubscription(),
                                        student.getCode()),
                                student.getName()
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    public Student findStudent(Integer subscription, Integer code) {
        return studentRepository.findBySubscriptionAndCode(subscription, code).orElse(null);
    }

    @Override
    public String nameNormalization(String name) throws InvalidSuppliedDataException {
        if (name == null) {
            throw new InvalidSuppliedDataException("Argument name cannot be null.");
        }
        return name.trim().toUpperCase();
    }

    @Override
    public void validateName(String name) throws InvalidSuppliedDataException {
        if (name == null || name.isEmpty()) {
            throw new InvalidSuppliedDataException("Argument name cannot be neither null nor empty.");
        }

        if (!Pattern.compile("[A-Z]{3,}(\\s[A-Z]{3,})+").matcher(name).matches()) {
            throw new InvalidSuppliedDataException("Invalid content provided on argument name.");
        }
    }

    @Override
    public void checkForConflictOnInsert(String name) throws StudentNameConflictException {
        Integer countByName = studentRepository.countByName(name).orElse(0);

        if (countByName > 0) {
            throw new StudentNameConflictException("Student with name " + name + " is already recorded.");
        }
    }

    @Override
    public Integer highestSubscriptionValue() {
        return studentRepository.highestSubscriptionValue().orElse(0);
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
