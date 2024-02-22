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
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JpaPagingJobConfig {
    private final EntityManagerFactory entityManagerFactory;
    private final JobParameter jobParameter;
    private static final int chunkSize = 50000;

    @Bean
    public Job jpaPagingJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("jpaPagingJob", jobRepository)
                .start(jpaPagingStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    @JobScope
    public Step jpaPagingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("jpaPagingStep", jobRepository)
                .<Member, Member>chunk(chunkSize, transactionManager)
                .reader(jpaPagingItemReader())
                .processor(jpaPagingItemProcessor())
                .writer(jpaPagingItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Member> jpaPagingItemReader() {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("birthDay", jobParameter.getBirthDay());

        return new JpaPagingItemReaderBuilder<Member>()
                .name("jpaPagingItemReader")
                .queryString("select m from Member m where m.birthDay = :birthDay")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .parameterValues(parameterValues)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Member, Member> jpaPagingItemProcessor() {
        return member -> {
            member.changeStatus(MemberStatus.WITHDRAW);
            return member;
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<Member> jpaPagingItemWriter() {
        return new JpaItemWriterBuilder<Member>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
