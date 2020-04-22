package org.fiap.test.spring.student.domain;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.application.StudentActuator;
import org.fiap.test.spring.student.domain.exception.StudentNameConflictException;
import org.fiap.test.spring.student.domain.usecase.StudentUseCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentActuator studentActuator;

    @Captor
    private ArgumentCaptor<Student> studentArgumentCaptor;

    private StudentService studentService;

    @Before
    public void setUp() throws Exception {
        studentService = new StudentServiceImpl(studentRepository, studentActuator);

        when(studentRepository.countByName("ZELIA SOMEKH")).thenReturn(1);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void check_for_invalid_input_null_pointer_exception() {
        studentService.validateFileLineContent(null);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void check_for_invalid_input_empty_string() {
        studentService.validateFileLineContent("");
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void check_for_invalid_input_trailing_dashes() {
        studentService.validateFileLineContent("---------------------------A---------------------------");
    }

    @Test
    public void check_for_valid_input_no_exception_is_raised() {
        // given
        String lineContent = "ZELIA MERBACH GOMES SOMEKH               9999999 999-99";

        // when
        studentService.validateFileLineContent(lineContent);
    }

    @Test
    public void parse_student_name() {
        // given
        String lineContent = "ZELIA MERBACH GOMES SOMEKH               9999999 999-99";

        // when
        String studentName = studentService.parseStudentName(lineContent);

        // then
        assertThat(studentName).isEqualTo("ZELIA MERBACH GOMES SOMEKH");
    }

    @Test
    public void parse_student_subscription() {
        // given
        String lineContent = "ZELIA MERBACH GOMES SOMEKH               9999999 999-99";

        // when
        Integer studentSubscription = studentService.parseStudentSubscription(lineContent);

        // then
        assertThat(studentSubscription).isEqualTo(9999999);
    }

    @Test
    public void parse_student_code() {
        // given
        String lineContent = "ZELIA MERBACH GOMES SOMEKH               9999999 999-99";

        // when
        Integer studentCode = studentService.parseStudentCode(lineContent);

        // then
        assertThat(studentCode).isEqualTo(99999);
    }

    @Test
    public void insert_student_based_on_a_StudentBatchParsedContent_instance() {
        // given
        StudentUseCase.StudentBatchParsedContent studentBatchParsedContent = new StudentUseCase.StudentBatchParsedContent(
                "ZELIA MERBACH GOMES SOMEKH",
                9999999,
                99999
        );

        // when
        studentService.insert(studentBatchParsedContent);

        // then
        verify(studentRepository, times(1)).save(studentArgumentCaptor.capture());
        Student student = studentArgumentCaptor.getValue();

        assertThat(studentBatchParsedContent.getName()).isEqualTo(student.getName());
        assertThat(studentBatchParsedContent.getSubscription()).isEqualTo(student.getSubscription());
        assertThat(studentBatchParsedContent.getCode()).isEqualTo(student.getCode());
    }

    @Test
    public void insert_student_based_on_data_provided_as_a_list_of_parameters() {
        // when
        studentService.insert(
                "ZELIA MERBACH GOMES SOMEKH",
                9999999,
                99999
        );

        // then
        verify(studentRepository, times(1)).save(studentArgumentCaptor.capture());
        Student student = studentArgumentCaptor.getValue();

        assertThat("ZELIA MERBACH GOMES SOMEKH").isEqualTo(student.getName());
        assertThat(9999999).isEqualTo(student.getSubscription());
        assertThat(99999).isEqualTo(student.getCode());
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void provide_null_to_name_normalization_method() {
        studentService.nameNormalization(null);
    }

    @Test
    public void check_for_empty_string_on_name_normalization() {
        String nameNormalization = studentService.nameNormalization("");

        assertThat(nameNormalization).isEmpty();
    }

    @Test
    public void check_for_name_normalization_with_trailing_spaces_and_lower_case_content() {
        String nameNormalization = studentService.nameNormalization(" zelia merbach gomes somekh ");

        assertThat(nameNormalization).isEqualTo("ZELIA MERBACH GOMES SOMEKH");
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void validate_name_with_null_value() {
        studentService.validateName(null);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void validate_name_empty_string() {
        studentService.validateName("");
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void validate_name_without_at_least_first_and_last_name() {
        studentService.validateName("ZELIA");
    }

    @Test
    public void validate_name_with_first_and_last_names() {
        studentService.validateName("ZELIA SOMEKH");
    }

    @Test
    public void validate_name_with_full_name() {
        studentService.validateName("ZELIA MERBACH GOMES SOMEKH");
    }

    @Test(expected = StudentNameConflictException.class)
    public void check_for_existing_conflict() {
        studentService.checkForConflictOnInsert("ZELIA SOMEKH");
    }

    @Test
    public void check_for_non_conflict_found() {
        studentService.checkForConflictOnInsert("ZELIA MERBACH GOMES SOMEKH");
    }

    @Test
    public void check_random_code_is_within_specified_range() {
        Integer min = 10_000;
        Integer max = 99_999;

        for (int i = min; i <= max; i++) {
            Integer randomCodeValue = studentService.randomCodeValue();
            assertThat(randomCodeValue).isBetween(min, max);
        }
    }

    @Test
    public void formats_subscription_and_code_generating_an_student_identifier() {
        String studentIdentification = studentService.formatIdentification(1_234_567, 12_345);

        assertThat(studentIdentification).isEqualTo("1234567 123-45");
    }
}