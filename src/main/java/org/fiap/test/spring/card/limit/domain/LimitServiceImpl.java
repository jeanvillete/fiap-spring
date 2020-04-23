package org.fiap.test.spring.card.limit.domain;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
class LimitServiceImpl implements LimitService {

    private final LimitRepository limitRepository;

    LimitServiceImpl(LimitRepository limitRepository) {
        this.limitRepository = limitRepository;
    }

    @Override
    public void validateMinimumLimitValue(BigDecimal value) throws InvalidSuppliedDataException {
        if (value == null) {
            throw new InvalidSuppliedDataException("The argument value is mandatory, it cannot be null.");
        }
        if (value.compareTo(new BigDecimal("0")) == -1) {
            throw new InvalidSuppliedDataException("The argument value must be greater or equals zero (0).");
        }
    }

    @Override
    @Transactional
    public void insert(Limit limit) {
        limitRepository.save(limit);
    }

}
