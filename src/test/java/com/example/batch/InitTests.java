package com.example.batch;

import com.example.batch.member.entity.Member;
import com.example.batch.member.enumeration.MemberStatus;
import com.example.batch.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
public class InitTests {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
//    @Rollback(value = false)
    public void init() {
        for (int i = 0; i < 500; i++) {
            List<Member> members = new LinkedList<>();
            for (int j = 1; j <= 10000; j++) {
                members.add(
                        Member.builder()
                                .name("사람")
                                .status(MemberStatus.SLEEP)
                                .birthDay(LocalDate.of(1995, 12, 8))
                                .build()
                );
            }
            memberRepository.saveAllAndFlush(members);
        }

//        List<Member> members = new LinkedList<>();
//        for (int i = 1000001; i <= 5000000; i++) {
//            members.add(
//                    Member.builder()
//                            .name("사람" + i)
//                            .status(MemberStatus.SLEEP)
//                            .birthDay(LocalDate.of(1995, 12, 8))
//                            .build()
//            );
//        }
//        memberRepository.saveAll(members);

//        memberRepository.saveAll(
//                IntStream.iterate(1, i -> i + 1)
//                        .limit(9999950)
//                        .mapToObj(i -> Member.builder()
//                                .name("사람" + i)
//                                .status(MemberStatus.SLEEP)
//                                .build()
//                        )
//                        .collect(Collectors.toList())
//        );
    }
}
