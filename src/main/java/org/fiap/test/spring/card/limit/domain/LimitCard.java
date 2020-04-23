package org.fiap.test.spring.card.limit.domain;

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

    @Column(name = "student_id")
    private Integer student;

    LimitCard() {
    }

    LimitCard(Integer id, LocalDateTime date, BigDecimal value, Integer student) {
        this.id = id;
        this.date = date;
        this.value = value;
        this.student = student;
    }

    public LimitCard(BigDecimal value, Integer student) {
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

    public Integer getStudent() {
        return student;
    }

    void setStudent(Integer student) {
        this.student = student;
    }
}
