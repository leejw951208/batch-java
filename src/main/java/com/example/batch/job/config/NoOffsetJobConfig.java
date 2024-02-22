package com.example.batch.job.config;

import com.example.batch.job.parameter.JobParameter;
import com.example.batch.job.reader.nooffset.NumberNoOffset;
import com.example.batch.job.reader.nooffset.repository.NoOffsetRepository;
import com.example.batch.member.entity.Member;
import com.example.batch.member.entity.QMember;
import com.example.batch.member.enumeration.MemberStatus;
import com.example.batch.member.repository.MemberRepository;
import com.example.batch.job.reader.nooffset.NoOffsetRepositoryItemReader;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NoOffsetJobConfig extends DefaultBatchConfiguration {
    private static final int chunkSize = 50000;

    private final JobParameter jobParameter;
    private final MemberRepository memberRepository;
    private final NoOffsetRepository noOffsetRepository;

    @Bean
    public Job noOffsetJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("noOffsetJob", jobRepository)
                .start(noOffsetStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    @JobScope
    public Step noOffsetStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("noOffsetStep", jobRepository)
                .<Member, Member>chunk(chunkSize, transactionManager)
                .reader(noOffsetItemReader())
                .processor(noOffsetItemProcessor())
                .writer(noOffsetItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public NoOffsetRepositoryItemReader<Member> noOffsetItemReader() {
        QMember qMember = new QMember("member");

        NoOffsetRepositoryItemReader<Member> reader = new NoOffsetRepositoryItemReader<>();
        reader.setName("noOffsetItemReader");
        reader.setRepository(memberRepository);
        reader.setMethodName("findNoOffsetMadeByQueryDsl");
        reader.setPageSize(chunkSize);
        reader.setArguments(List.of(qMember.birthDay.eq(jobParameter.getBirthDay())));
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        reader.setNoOffset(new NumberNoOffset<>(qMember, qMember.id, noOffsetRepository));

        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Member, Member> noOffsetItemProcessor() {
        return member -> {
            member.changeStatus(MemberStatus.WITHDRAW);
            return member;
        };
    }

    @Bean
    @StepScope
    public RepositoryItemWriter<Member> noOffsetItemWriter() {
        return new RepositoryItemWriterBuilder<Member>()
                .repository(memberRepository)
                .build();
    }
}
