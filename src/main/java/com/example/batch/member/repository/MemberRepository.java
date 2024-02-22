package com.example.batch.member.repository;

import com.example.batch.member.entity.Member;
import com.example.batch.member.repository.querydsl.MemberQueryDslRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryDslRepository {
    Page<Member> findByBirthDay(LocalDate birthDay, Pageable pageable);
}
