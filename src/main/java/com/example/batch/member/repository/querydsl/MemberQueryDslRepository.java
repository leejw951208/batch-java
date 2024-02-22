package com.example.batch.member.repository.querydsl;

import com.example.batch.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberQueryDslRepository {
    Page<Member> findZeroOffsetMadeByQueryDsl(BooleanExpression expression, Pageable pageable);

    Page<Member> findNoOffsetMadeByQueryDsl(BooleanExpression expression, Long lastValue, Pageable pageable);
}
