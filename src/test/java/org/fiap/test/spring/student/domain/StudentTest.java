package org.fiap.test.spring.student.domain;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class StudentTest {

    @Test(expected = IllegalArgumentException.class)
    public void check_for_invalid_input_null_pointer_exception() {
        new Student(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_for_invalid_input_empty_string() {
        new Student("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_for_invalid_input_trailing_dashes() {
        new Student("---------------------------A---------------------------");
    }

    @Test
    public void check_for_valid_input() {
        Student instantiatedStudent = new Student(
                "ZELIA MERBACH GOMES SOMEKH",
                9999999,
                99999
        );
        Student parsedStudent = new Student("ZELIA MERBACH GOMES SOMEKH               9999999 999-99");

        Assertions.assertThat(parsedStudent).isEqualTo(instantiatedStudent);
    }
}