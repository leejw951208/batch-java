package com.example.batch.member.repository.querydsl.impl;

import com.example.batch.member.entity.Member;
import com.example.batch.member.entity.QMember;
import com.example.batch.member.repository.querydsl.MemberQueryDslRepository;
import com.example.batch.util.OrderByUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryDslRepositoryImpl implements MemberQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    private static final QMember qMember = new QMember("member");

    @Override
    public Page<Member> findZeroOffsetMadeByQueryDsl(BooleanExpression expression, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(expression);

        List<Member> contents = queryFactory
                .selectFrom(qMember)
                .where(expression)
                .orderBy(OrderByUtils.dynamicOrderBy(pageable, qMember))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(contents, pageable, totalCount(builder)::fetchOne);
    }

    @Override
    public Page<Member> findNoOffsetMadeByQueryDsl(BooleanExpression expression, Long lastValue, Pageable pageable) {
        // lastValue == null : 조회 데이터가 없는 경우
        if (lastValue == null) return new PageImpl<>(new ArrayList<>(), pageable, 0L);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(expression);

        Order direction = OrderByUtils.direction(pageable);

        // 정렬 조건에 따라
        if (Order.ASC.equals(direction)) builder.and(qMember.id.goe(lastValue));
        else builder.and(qMember.id.loe(lastValue));

        List<Member> contents = queryFactory
                .selectFrom(qMember)
                .where(builder)
                .orderBy(OrderByUtils.dynamicOrderBy(pageable, qMember))
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(contents, pageable, totalCount(builder)::fetchOne);
    }

    private JPAQuery<Long> totalCount(BooleanBuilder builder) {
        return queryFactory
                .select(qMember.count())
                .from(qMember)
                .where(builder);
    }
}
