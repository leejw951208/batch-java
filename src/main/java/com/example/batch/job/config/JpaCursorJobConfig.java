package com.example.batch.job.config;

import com.example.batch.job.parameter.JobParameter;
import com.example.batch.member.entity.Member;
import com.example.batch.member.enumeration.MemberStatus;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JpaCursorJobConfig {
    private final EntityManagerFactory entityManagerFactory;
    private final JobParameter jobParameter;
    private static final int chunkSize = 50000;

    @Bean
    public Job jpaCursorJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new JobBuilder("jpaCursorJob", jobRepository)
                .start(jpaCursorStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    @JobScope
    public Step jpaCursorStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new StepBuilder("jpaCursorStep", jobRepository)
                .<Member, Member>chunk(chunkSize, transactionManager)
                .reader(jpaCursorReader())
                .processor(jpaCursorProcessor())
                .writer(jpaCursorWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<Member> jpaCursorReader() {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("birthDay", jobParameter.getBirthDay());

        return new JpaCursorItemReaderBuilder<Member>()
                .name("jpaCursorReader")
                .queryString("select m from Member m where m.birthDay = :birthDay")
                .entityManagerFactory(entityManagerFactory)
                .parameterValues(parameterValues)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Member, Member> jpaCursorProcessor() {
        return member -> {
            member.changeStatus(MemberStatus.WITHDRAW);
            return member;
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<Member> jpaCursorWriter() {
        return new JpaItemWriterBuilder<Member>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
