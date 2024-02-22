package com.example.batch;

import com.example.batch.member.entity.Member;
import com.example.batch.member.entity.QMember;
import com.example.batch.job.reader.nooffset.repository.NoOffsetRepositoryImpl;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
class BatchApplicationTests {

    @Test
    public void test() {
    }
}
