package org.fiap.test.spring.student.domain;

import javax.persistence.*;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 7)
    private Integer subscription;

    @Column(nullable = false, length = 5)
    private Integer code;

    Student() {
    }

    Student(Integer id, String name, Integer subscription, Integer code) {
        this.id = id;
        this.name = name;
        this.subscription = subscription;
        this.code = code;
    }

    Student(String name, Integer subscription, Integer code) {
        this.name = name;
        this.subscription = subscription;
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return name.equals(student.name) &&
                subscription.equals(student.subscription) &&
                code.equals(student.code);
    }

    // GETTERS AND SETTERS //
    public Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public Integer getSubscription() {
        return subscription;
    }

    void setSubscription(Integer subscription) {
        this.subscription = subscription;
    }

    public Integer getCode() {
        return code;
    }

    void setCode(Integer code) {
        this.code = code;
    }
}
