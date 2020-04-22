package org.fiap.test.spring.student.application;

import org.fiap.test.spring.student.domain.usecase.StudentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("students")
public class StudentController {

    private final StudentUseCase studentUseCase;

    public StudentController(StudentUseCase studentUseCase) {
        this.studentUseCase = studentUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentUseCase.StudentCreated createStudent(@RequestBody StudentUseCase.StudentCreated payload) {
        return studentUseCase.createStudent(payload.getName());
    }
}
