package org.fiap.test.spring.card.limit.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "LIMIT_CARD")
public class Limit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "datetime_limit")
    private LocalDateTime date;

    @Column
    private BigDecimal value;

    @Column(name = "student_id")
    private Integer student;

    public Limit(BigDecimal value, Integer student) {
        this.date = LocalDateTime.now();
        this.value = value;
        this.student = student;
    }

    Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    LocalDateTime getDate() {
        return date;
    }

    void setDate(LocalDateTime date) {
        this.date = date;
    }

    BigDecimal getValue() {
        return value;
    }

    void setValue(BigDecimal value) {
        this.value = value;
    }

    Integer getStudent() {
        return student;
    }

    void setStudent(Integer student) {
        this.student = student;
    }
}
