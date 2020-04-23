package org.fiap.test.spring.card.limit.application;

import org.fiap.test.spring.card.limit.domain.usecase.LimitCardUseCase;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("students/{id}/card/limit")
public class LimitCardController {

    private final LimitCardUseCase limitCardUseCase;

    public LimitCardController(LimitCardUseCase limitCardUseCase) {
        this.limitCardUseCase = limitCardUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewLimitCard(@PathVariable String id, @RequestBody LimitCardUseCase.StudentLimitCardPayload studentLimitCardPayload) throws StudentNotFoundException, InvalidSuppliedDataException {
        limitCardUseCase.createNewLimitCard(id, studentLimitCardPayload);
    }

    @GetMapping
    public LimitCardUseCase.StudentLimitCardPayload getCurrentLimitCard(@PathVariable String id) throws StudentNotFoundException, InvalidSuppliedDataException {
        return limitCardUseCase.getCurrentCardLimit(id);
    }
}
