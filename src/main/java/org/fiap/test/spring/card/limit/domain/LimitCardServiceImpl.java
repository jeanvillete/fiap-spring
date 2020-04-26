package org.fiap.test.spring.card.limit.domain;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.Student;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
class LimitCardServiceImpl implements LimitCardService {

    private final LimitCardRepository limitCardRepository;

    LimitCardServiceImpl(LimitCardRepository limitCardRepository) {
        this.limitCardRepository = limitCardRepository;
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
    public void insert(LimitCard limitCard) {
        limitCardRepository.save(limitCard);
    }

    @Override
    public Optional<LimitCard> getCurrentLimitCard(Student student) {
        Optional<Integer> highestLimitId = Optional.ofNullable(
                limitCardRepository.maxIdByStudentId(student)
        );

        return limitCardRepository.findById(highestLimitId.orElse(0));
    }

}
