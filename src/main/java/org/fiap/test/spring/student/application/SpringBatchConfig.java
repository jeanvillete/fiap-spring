package org.fiap.test.spring.student.application;

import org.fiap.test.spring.common.exception.InvalidSuppliedDataException;
import org.fiap.test.spring.student.domain.usecase.StudentUseCase;
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

    private final StudentUseCase studentUseCase;

    public SpringBatchConfig(StudentUseCase studentUseCase) {
        this.studentUseCase = studentUseCase;
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
    public ItemProcessor<String, StudentUseCase.StudentBatchParsedContent> processor() {
        return lineContent -> {
            LOGGER.debug("Processing lineContent item {}", lineContent);

            try {
                return studentUseCase.parseBatchContent(lineContent);
            } catch (InvalidSuppliedDataException exception) {
                LOGGER.debug("Skipping item with invalid line content {}", lineContent);
                return null;
            }
        };
    }

    @Bean("studentItemWriter")
    public ItemWriter<StudentUseCase.StudentBatchParsedContent> itemWriterLogger() {
        return _students -> _students.stream()
                .peek(_student -> LOGGER.debug("Writing student item {}", _student))
                .forEach(studentUseCase::insertStudent);
    }

    @Bean("studentStep")
    public Step step(StepBuilderFactory stepBuilderFactory,
                     @Qualifier("studentFileReader") ItemReader<String> studentItemReader,
                     @Qualifier("studentItemProcessor") ItemProcessor<String, StudentUseCase.StudentBatchParsedContent> studentItemProcessor,
                     @Qualifier("studentItemWriter") ItemWriter<StudentUseCase.StudentBatchParsedContent> studentItemWriter) {
        return stepBuilderFactory.get("import students; step")
                .<String, StudentUseCase.StudentBatchParsedContent>chunk(50)
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
