package com.example.batch.job.reader.nooffset.repository;

import com.example.batch.job.reader.nooffset.repository.util.NumberNoOffsetRepositoryUtils;
import com.example.batch.util.OrderByUtils;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoOffsetRepositoryImpl implements NoOffsetRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Long findLastValue(BooleanExpression expression, Path<?> path, NumberPath<Long> numberPath, Long lastValue, Pageable pageable) {
        Order direction = OrderByUtils.direction(pageable);
        JPAQuery<?> from = queryFactory.from((EntityPath<?>) path);

        // pageable.hasPrevious == true && lastValue != null : 작업 시작
        // pageable.hasPrevious == true && lastValue == null : 작업 재시작
        //  - 작업 재시작의 경우 offset으로 실패 지점을 찾은 뒤 noOffset으로 데이터 조회
        if (pageable.hasPrevious()) {
            JPAQuery<?> query = from
                    .select(numberPath)
                    .where(NumberNoOffsetRepositoryUtils.createWhere(numberPath, expression, direction, lastValue))
                    .orderBy(OrderByUtils.dynamicOrderBy(pageable, path))
                    .limit(pageable.getPageSize());

            if (lastValue == null) query = addOffset(query, pageable);

            return NumberNoOffsetRepositoryUtils.getLastValue(query, lastValue == null);
        } else {
            return NumberNoOffsetRepositoryUtils.getInitValue(from.where(expression), numberPath, direction);
        }
    }

    private JPAQuery<?> addOffset(JPAQuery<?> query, Pageable pageable) {
        return query.offset(pageable.getOffset());
    }
}
