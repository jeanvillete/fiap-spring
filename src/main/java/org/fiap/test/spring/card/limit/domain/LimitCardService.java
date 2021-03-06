package org.fiap.test.spring.card.limit.domain;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;

import java.math.BigDecimal;
import java.util.Optional;

public interface LimitCardService {

    void validateMinimumLimitValue(BigDecimal value) throws InvalidSuppliedDataException;

    void insert(LimitCard limitCard);

    Optional<LimitCard> getCurrentLimitCard(Student student);
}
