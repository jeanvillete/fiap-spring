package org.fiap.test.spring.student.application;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Endpoint(id = "students")
public class StudentActuator {

    private AtomicInteger studentsCounter;

    public StudentActuator() {
        this.studentsCounter = new AtomicInteger();
    }

    @ReadOperation
    public int count() {
        return studentsCounter.get();
    }

    public void increment() {
        studentsCounter.getAndIncrement();
    }
}
