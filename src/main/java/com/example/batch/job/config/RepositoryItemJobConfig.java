package com.example.batch.job.config;

import com.example.batch.job.parameter.JobParameter;
import com.example.batch.member.entity.Member;
import com.example.batch.member.enumeration.MemberStatus;
import com.example.batch.member.repository.MemberRepository;
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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RepositoryItemJobConfig {
    private final MemberRepository memberRepository;
    private final JobParameter jobParameter;

    private static final int chunkSize = 50000;

    @Bean
    public Job repositoryItemJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new JobBuilder("repositoryItemJob", jobRepository)
                .start(repositoryItemStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    @JobScope
    public Step repositoryItemStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new StepBuilder("repositoryItemStep", jobRepository)
                .<Member, Member>chunk(chunkSize, transactionManager)
                .reader(repositoryItemReader())
                .processor(repositoryItemProcessor())
                .writer(repositoryItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Member> repositoryItemReader() {
        return new RepositoryItemReaderBuilder<Member>()
                .name("repositoryItemReader")
                .repository(memberRepository)
                .methodName("findByBirthDay")
                .pageSize(chunkSize)
                .arguments(List.of(jobParameter.getBirthDay()))
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Member, Member> repositoryItemProcessor() {
        return member -> {
            member.changeStatus(MemberStatus.WITHDRAW);
            return member;
        };
    }

    @Bean
    @StepScope
    public RepositoryItemWriter<Member> repositoryItemWriter() {
        return new RepositoryItemWriterBuilder<Member>()
                .repository(memberRepository)
                .build();
    }
}
