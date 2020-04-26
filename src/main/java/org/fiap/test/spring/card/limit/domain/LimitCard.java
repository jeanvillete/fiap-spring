package org.fiap.test.spring.card.limit.domain;

import org.fiap.test.spring.student.domain.Student;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class LimitCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "datetime_limit")
    private LocalDateTime date;

    @Column
    private BigDecimal value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    LimitCard() {
    }

    LimitCard(Integer id, LocalDateTime date, BigDecimal value, Student student) {
        this.id = id;
        this.date = date;
        this.value = value;
        this.student = student;
    }

    public LimitCard(BigDecimal value, Student student) {
        this.date = LocalDateTime.now();
        this.value = value;
        this.student = student;
    }

    public Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getValue() {
        return value;
    }

    void setValue(BigDecimal value) {
        this.value = value;
    }

    public Student getStudent() {
        return student;
    }

    void setStudent(Student student) {
        this.student = student;
    }
}
