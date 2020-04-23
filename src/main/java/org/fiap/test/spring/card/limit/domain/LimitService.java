package org.fiap.test.spring.card.limit.domain;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;

import java.math.BigDecimal;

public interface LimitService {

    void validateMinimumLimitValue(BigDecimal value) throws InvalidSuppliedDataException;

    void insert(Limit limit);

}
