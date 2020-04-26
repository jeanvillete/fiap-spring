package org.fiap.test.spring.student.domain;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.application.StudentActuator;
import org.fiap.test.spring.student.domain.exception.StudentNameConflictException;
import org.fiap.test.spring.student.domain.exception.StudentNotFoundException;
import org.fiap.test.spring.student.domain.usecase.StudentUseCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
    public void setUp() {
        studentService = new StudentServiceImpl(studentRepository, studentActuator);

        when(studentRepository.countByName("ZELIA SOMEKH"))
                .thenReturn(Optional.of(1));

        when(studentRepository.countBySubscriptionAndCode(1234567, 12345))
                .thenReturn(Optional.of(1));

        when(studentRepository.countBySubscriptionNotAndCodeNotAndName(1234567, 12345, "ZELIA SOMEKH"))
                .thenReturn(Optional.of(1));

        when(studentRepository.findBySubscriptionAndCode(1234567, 12345))
                .thenReturn(Optional.of(new Student(1,"ZELIA SOMEKH", 1234567, 12345)));

        when(studentRepository.findAllByNameContaining("SILVA")).thenReturn(
                Optional.of(
                        Arrays.asList(
                                new Student(1,"ADRIANA PATRICIA DE OLIVEIRA SILVA", 1111111, 11111),
                                new Student(2,"ADRIANE PENA DA SILVA", 2222222, 22222),
                                new Student(3,"ADRIANO RICARDO PALACIO DA SILVA", 3333333, 33333),
                                new Student(4,"YURI SILVA SOUSA", 4444444, 44444),
                                new Student(5,"YARA CRISTINA PEREIRA DA SILVA", 5555555, 55555)
                        )
                )
        );
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void check_for_invalid_input_null_pointer_exception() throws InvalidSuppliedDataException {
        studentService.validateFileLineContent(null);

        failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void check_for_invalid_input_empty_string() throws InvalidSuppliedDataException {
        studentService.validateFileLineContent("");

        failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void check_for_invalid_input_trailing_dashes() throws InvalidSuppliedDataException {
        studentService.validateFileLineContent("---------------------------A---------------------------");

        failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test
    public void check_for_valid_input_no_exception_is_raised() throws InvalidSuppliedDataException {
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
    public void provide_null_to_name_normalization_method() throws InvalidSuppliedDataException {
        studentService.nameNormalization(null);

        failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test
    public void check_for_empty_string_on_name_normalization() throws InvalidSuppliedDataException {
        String nameNormalization = studentService.nameNormalization("");

        assertThat(nameNormalization).isEmpty();
    }

    @Test
    public void check_for_name_normalization_with_trailing_spaces_and_lower_case_content() throws InvalidSuppliedDataException {
        String nameNormalization = studentService.nameNormalization(" zelia merbach gomes somekh ");

        assertThat(nameNormalization).isEqualTo("ZELIA MERBACH GOMES SOMEKH");
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void validate_name_with_null_value() throws InvalidSuppliedDataException {
        studentService.validateName(null);

        failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void validate_name_empty_string() throws InvalidSuppliedDataException {
        studentService.validateName("");

        failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void validate_name_without_at_least_first_and_last_name() throws InvalidSuppliedDataException {
        studentService.validateName("ZELIA");

        failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test
    public void validate_name_with_first_and_last_names() throws InvalidSuppliedDataException {
        studentService.validateName("ZELIA SOMEKH");
    }

    @Test
    public void validate_name_with_full_name() throws InvalidSuppliedDataException {
        studentService.validateName("ZELIA MERBACH GOMES SOMEKH");
    }

    @Test(expected = StudentNameConflictException.class)
    public void check_for_existing_conflict() throws StudentNameConflictException {
        studentService.checkForConflictOnInsert("ZELIA SOMEKH");
    }

    @Test
    public void check_for_non_conflict_found() throws StudentNameConflictException {
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

    @Test(expected = InvalidSuppliedDataException.class)
    public void parse_student_id_with_null_value() throws InvalidSuppliedDataException {
        studentService.parseStudentId(null);

        failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void parse_student_id_with_empty_string() throws InvalidSuppliedDataException {
        studentService.parseStudentId("");

        failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void parse_student_id_with_invalid_content() throws InvalidSuppliedDataException {
        studentService.parseStudentId("12345 12-1");

        failBecauseExceptionWasNotThrown(InvalidSuppliedDataException.class);
    }

    @Test
    public void parse_student_id_with_valid_content() throws InvalidSuppliedDataException {
        StudentId studentId = studentService.parseStudentId("1234567 123-45");

        assertThat(studentId.getSubscription()).isEqualTo(1234567);
        assertThat(studentId.getCode()).isEqualTo(12345);
    }

    @Test(expected = StudentNotFoundException.class)
    public void invoke_ensure_student_is_found_with_non_existing_student_id() throws StudentNotFoundException {
        studentService.ensureStudentIsFound(7654321, 54321);

        failBecauseExceptionWasNotThrown(StudentNotFoundException.class);
    }

    @Test
    public void invoke_ensure_student_is_found_with_an_existing_and_valid_student_id() throws StudentNotFoundException {
        studentService.ensureStudentIsFound(1234567, 12345);
    }

    @Test(expected = StudentNameConflictException.class)
    public void check_for_client_on_update_with_name_already_in_use() throws StudentNameConflictException {
        studentService.checkForConflictOnUpdate(1234567, 12345, "ZELIA SOMEKH");

        failBecauseExceptionWasNotThrown(StudentNameConflictException.class);
    }

    @Test
    public void check_for_client_on_update_with_name_available_to_be_used() throws StudentNameConflictException {
        studentService.checkForConflictOnUpdate(1234567, 12345, "ZELIA MERBACH GOMES SOMEKH");
    }

    @Test
    public void update_student_name() {
        // given
        String nameToBeUpdated = "ZELIA MERBACH GOMES SOMEKH";

        // when
        studentService.updateName(1234567, 12345, nameToBeUpdated);

        // then
        verify(studentRepository, times(1)).save(studentArgumentCaptor.capture());
        Student student = studentArgumentCaptor.getValue();

        assertThat(1).isEqualTo(student.getId());
        assertThat(nameToBeUpdated).isEqualTo(student.getName());
        assertThat(1234567).isEqualTo(student.getSubscription());
        assertThat(12345).isEqualTo(student.getCode());
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void search_for_students_with_null_name() throws InvalidSuppliedDataException {
        studentService.searchByName(null);
    }

    @Test(expected = InvalidSuppliedDataException.class)
    public void search_for_students_with_empty_string_for_name() throws InvalidSuppliedDataException {
        studentService.searchByName("");
    }

    @Test
    public void search_for_students_with_valid_name() throws InvalidSuppliedDataException {
        List<StudentUseCase.StudentPayload> students = studentService.searchByName("SILVA");

        assertThat(students)
                .hasSize(5)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple( "1111111 111-11", "ADRIANA PATRICIA DE OLIVEIRA SILVA"),
                        tuple( "2222222 222-22", "ADRIANE PENA DA SILVA"),
                        tuple( "3333333 333-33", "ADRIANO RICARDO PALACIO DA SILVA"),
                        tuple( "4444444 444-44", "YURI SILVA SOUSA"),
                        tuple( "5555555 555-55", "YARA CRISTINA PEREIRA DA SILVA")
                );
    }

    @Test
    public void try_find_student_but_providing_invalid_parameter() {
        Student student = studentService.findStudent(7654321, 54321);

        assertThat(student).isNull();
    }

    @Test
    public void try_find_student_providing_valid_parameters() {
        Student student = studentService.findStudent(1234567, 12345);

        assertThat(student.getId()).isEqualTo(1);
        assertThat(student.getName()).isEqualTo("ZELIA SOMEKH");
        assertThat(student.getSubscription()).isEqualTo(1234567);
        assertThat(student.getCode()).isEqualTo(12345);
    }
}