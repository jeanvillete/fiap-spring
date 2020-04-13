package org.fiap.test.spring.student.domain;

import org.fiap.test.spring.student.application.StudentActuator;
import org.springframework.stereotype.Service;

@Service
class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentActuator studentActuator;

    public StudentServiceImpl(StudentRepository studentRepository, StudentActuator studentActuator) {
        this.studentRepository = studentRepository;
        this.studentActuator = studentActuator;
    }

    @Override
    public void insert(Student student) {
        this.studentRepository.save(student);
        this.studentActuator.increment();
    }
}
