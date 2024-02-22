package com.example.batch.job.config;

import com.example.batch.job.parameter.JobParameter;
import com.example.batch.job.reader.zerooffset.ZeroOffsetRepositoryItemReader;
import com.example.batch.member.entity.Member;
import com.example.batch.member.entity.QMember;
import com.example.batch.member.enumeration.MemberStatus;
import com.example.batch.member.repository.MemberRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ZeroOffsetJobConfig extends DefaultBatchConfiguration {
    private static final int chunkSize = 50000;

    private final JobParameter jobParameter;
    private final MemberRepository memberRepository;

    @Bean
    public Job zeroOffsetJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("zeroOffsetJob", jobRepository)
                .start(zeroOffsetStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    @JobScope
    public Step zeroOffsetStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("zeroOffsetStep", jobRepository)
                .<Member, Member>chunk(chunkSize, transactionManager)
                .reader(zeroOffsetItemReader())
                .processor(zeroOffsetItemProcessor())
                .writer(zeroOffsetItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public ZeroOffsetRepositoryItemReader<Member> zeroOffsetItemReader() {
        QMember qMember = new QMember("member");

        ZeroOffsetRepositoryItemReader<Member> reader = new ZeroOffsetRepositoryItemReader<>();
        reader.setRepository(memberRepository);
        reader.setMethodName("findZeroOffsetMadeByQueryDsl");
        reader.setPageSize(chunkSize);
        reader.setArguments(List.of(qMember.status.eq(jobParameter.getStatus())));
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        reader.setName("zeroOffsetItemReader");
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Member, Member> zeroOffsetItemProcessor() {
        return member -> {
            member.changeStatus(MemberStatus.WITHDRAW);
            return member;
        };
    }

    @Bean
    @StepScope
    public RepositoryItemWriter<Member> zeroOffsetItemWriter() {
        return new RepositoryItemWriterBuilder<Member>()
                .repository(memberRepository)
                .build();
    }
}
