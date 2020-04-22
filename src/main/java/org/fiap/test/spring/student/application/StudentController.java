package org.fiap.test.spring.student.application;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.exception.StudentNameConflictException;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.fiap.test.spring.student.domain.usecase.StudentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("students")
public class StudentController {

    private final StudentUseCase studentUseCase;

    public StudentController(StudentUseCase studentUseCase) {
        this.studentUseCase = studentUseCase;
    }

    @GetMapping
    public List<StudentUseCase.StudentPayload> searchStudentsByName(@RequestParam String name) throws InvalidSuppliedDataException {
        return studentUseCase.searchStudentsByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentUseCase.StudentPayload createStudent(@RequestBody StudentUseCase.StudentPayload payload) throws StudentNameConflictException, InvalidSuppliedDataException {
        return studentUseCase.createStudent(payload.getName());
    }

    @PatchMapping
    @RequestMapping("{id}")
    public void updateStudentName(@PathVariable String id, @RequestBody StudentUseCase.StudentPayload payload) throws StudentNameConflictException, StudentNotFoundException, InvalidSuppliedDataException {
        studentUseCase.updateStudentName(id, payload);
    }
}
