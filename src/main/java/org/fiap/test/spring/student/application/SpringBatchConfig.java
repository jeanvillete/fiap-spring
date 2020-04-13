package org.fiap.test.spring.student.application;

import org.fiap.test.spring.student.domain.Student;
import org.fiap.test.spring.student.domain.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class SpringBatchConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBatchConfig.class);

    private final StudentService studentService;

    public SpringBatchConfig(StudentService studentService) {
        this.studentService = studentService;
    }

    @Bean("studentFileReader")
    public FlatFileItemReader<String> fileReader(@Value("classpath:lista_alunos.txt") Resource resource) {
        LOGGER.debug("Initializing bean; studentFileReader");

        return new FlatFileItemReaderBuilder<String>()
                .name("import students; item reader")
                .resource(resource)
                .lineMapper((line, lineNumber) -> line)
                .build();
    }

    @Bean("studentItemProcessor")
    public ItemProcessor<String, Student> processor() {
        return lineContent -> {
            LOGGER.debug("Processing lineContent item {}", lineContent);

            try {
                return new Student(lineContent);
            } catch (IllegalArgumentException exception) {
                LOGGER.debug("Skipping item with invalid line content {}", lineContent);
                return null;
            }
        };
    }

    @Bean("studentItemWriter")
    public ItemWriter<Student> itemWriterLogger() {
        return _students -> _students.stream()
                .peek(_student -> LOGGER.debug("Writing student item {}", _student))
                .forEach(studentService::insert);
    }

    @Bean("studentStep")
    public Step step(StepBuilderFactory stepBuilderFactory,
                     @Qualifier("studentFileReader") ItemReader<String> studentItemReader,
                     @Qualifier("studentItemProcessor") ItemProcessor<String, Student> studentItemProcessor,
                     @Qualifier("studentItemWriter") ItemWriter<Student> studentItemWriter) {
        return stepBuilderFactory.get("import students; step")
                .<String, Student>chunk(50)
                .reader(studentItemReader)
                .processor(studentItemProcessor)
                .writer(studentItemWriter)
                .build();
    }

    @Bean("studentJob")
    public Job job(JobBuilderFactory jobBuilderFactory,
                   @Qualifier("studentStep") Step studentStep) {
        return jobBuilderFactory.get("import students; job")
                .start(studentStep)
                .build();
    }
}
