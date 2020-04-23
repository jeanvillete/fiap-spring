package org.fiap.test.spring.card.limit.application;

import org.fiap.test.spring.card.limit.domain.usecase.LimitUseCase;
import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("students")
public class LimitController {

    private final LimitUseCase limitUseCase;

    public LimitController(LimitUseCase limitUseCase) {
        this.limitUseCase = limitUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping("{id}/card/limit")
    public void createNewCardLimit(@PathVariable String id, @RequestBody LimitUseCase.StudentLimitPayload studentLimitPayload) throws StudentNotFoundException, InvalidSuppliedDataException {
        limitUseCase.createNewCardLimit(id, studentLimitPayload);
    }

}
