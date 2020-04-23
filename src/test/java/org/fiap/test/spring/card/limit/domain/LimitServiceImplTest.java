package org.fiap.test.spring.card.limit.domain;

import org.assertj.core.api.Assertions;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class LimitServiceImplTest {

    @Mock
    private LimitRepository limitRepository;

    private LimitService limitService;

    @Before
    public void setUp() throws Exception {
        limitService = new LimitServiceImpl(limitRepository);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void validate_minimum_value_providing_null() throws InvalidSuppliedDataException {
        limitService.validateMinimumLimitValue(null);

        Assertions.failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void validate_minimum_value_providing_a_negative_one() throws InvalidSuppliedDataException {
        limitService.validateMinimumLimitValue(new BigDecimal("-1"));

        Assertions.failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test
    public void validate_minimum_value_providing_a_value_one() throws InvalidSuppliedDataException {
        limitService.validateMinimumLimitValue(new BigDecimal("1"));
    }
}