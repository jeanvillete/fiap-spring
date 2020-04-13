package org.fiap.test.spring.student.domain;

import javax.persistence.*;
import java.util.regex.Pattern;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 10)
    private String subscription;

    @Column(nullable = false, length = 10)
    private String code;

    public Student() {
    }

    public Student(String lineContent) {
        if (lineContent == null || lineContent.isEmpty()) {
            throw new IllegalArgumentException("Line content is null or empty.");
        }
        if (lineContent.length() != 55) {
            throw new IllegalArgumentException("Line content is not 55 length.");
        }

        String name = lineContent.substring(0, 41).trim();
        if (!Pattern.compile("[\\w\\s]+").matcher(name).matches()) {
            throw new IllegalArgumentException("There's no name value on the argument line content.");
        }

        this.name = name;
        this.subscription = lineContent.substring(41, 48);
        this.code = lineContent.substring(49, 55);
    }

    public Student(String name, String subscription, String code) {
        this.name = name;
        this.subscription = subscription;
        this.code = code;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", subscription='" + subscription + '\'' +
                ", code='" + code + '\'' +
                '}';
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

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
