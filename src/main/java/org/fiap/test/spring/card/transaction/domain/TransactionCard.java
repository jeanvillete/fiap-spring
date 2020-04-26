package org.fiap.test.spring.card.transaction.domain;

import org.fiap.test.spring.card.limit.domain.LimitCard;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class TransactionCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 15)
    private String uuid;

    @Column(name = "datetime_transaction")
    private LocalDateTime date;

    @Column
    private BigDecimal value;

    @Column(length = 150)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_limit_id")
    private LimitCard limitCard;

    TransactionCard() {
    }

    TransactionCard(BigDecimal value, String description, LimitCard limitCard) {
        date = LocalDateTime.now();
        this.value = value;
        this.description = description;
        this.limitCard = limitCard;
    }

    public static TransactionCard debit(BigDecimal value, String description, LimitCard currentLimitCard) {
        return new TransactionCard(value, description, currentLimitCard);
    }

    public static TransactionCard chargeBack(BigDecimal value, String description, LimitCard currentLimitCard) {
        return new TransactionCard(value, description, currentLimitCard);
    }

    public static TransactionCard billPayment(BigDecimal value, String description, LimitCard currentLimitCard) {
        return new TransactionCard(value, description, currentLimitCard);
    }

    TransactionCard generateUUID() {
        this.uuid = UUID.randomUUID().toString().substring(0, 15);

        return this;
    }

    public Integer getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public LimitCard getLimitCard() {
        return limitCard;
    }
}
