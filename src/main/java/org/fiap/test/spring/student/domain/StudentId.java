package org.fiap.test.spring.student.domain;

public class StudentId {

    private Integer subscription;
    private Integer code;

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