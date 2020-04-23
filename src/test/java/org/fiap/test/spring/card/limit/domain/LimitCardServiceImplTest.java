package org.fiap.test.spring.card.limit.domain;

import org.assertj.core.api.Assertions;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LimitCardServiceImplTest {

    @Mock
    private LimitCardRepository limitCardRepository;

    private LimitCardService limitCardService;

    @Before
    public void setUp() throws Exception {
        limitCardService = new LimitCardServiceImpl(limitCardRepository);

        when(limitCardRepository.maxIdByStudentId(1)).thenReturn(10);
        when(limitCardRepository.maxIdByStudentId(2)).thenReturn(20);

        when(limitCardRepository.findById(10)).thenReturn(Optional.empty());
        when(limitCardRepository.findById(20))
                .thenReturn(
                        Optional.of(
                                new LimitCard(
                                        123,
                                        LocalDateTime.now(),
                                        new BigDecimal("53.11"),
                                        987
                                )
                        )
                );
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void validate_minimum_value_providing_null() throws InvalidSuppliedDataException {
        limitCardService.validateMinimumLimitValue(null);

        Assertions.failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void validate_minimum_value_providing_a_negative_one() throws InvalidSuppliedDataException {
        limitCardService.validateMinimumLimitValue(new BigDecimal("-1"));

        Assertions.failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test
    public void validate_minimum_value_providing_a_value_one() throws InvalidSuppliedDataException {
        limitCardService.validateMinimumLimitValue(new BigDecimal("1"));
    }

    @Test
    public void get_current_limit_card_with_empty_result() {
        Optional<LimitCard> currentLimitCard = limitCardService.getCurrentLimitCard(1);

        assertThat(currentLimitCard.isPresent()).isFalse();
    }

    @Test
    public void get_current_limit_card_with_valid_result() {
        Optional<LimitCard> _currentLimitCard = limitCardService.getCurrentLimitCard(2);

        LimitCard currentLimitCard = _currentLimitCard.get();

        assertThat(currentLimitCard.getId()).isEqualTo(123);
        assertThat(currentLimitCard.getValue()).isEqualTo(new BigDecimal("53.11"));
        assertThat(currentLimitCard.getStudent()).isEqualTo(987);
    }
}