package com.example.batch.member.service;

import com.example.batch.member.entity.Member;
import com.example.batch.member.enumeration.MemberStatus;
import com.example.batch.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {;

    @Transactional
    public void update(Member member) throws InterruptedException {
//        if (member.getId() == 21) {
//            throw new RuntimeException();
//        }
        member.changeStatus(MemberStatus.WITHDRAW);

//        memberHistoryService.save(member);
    }
}
