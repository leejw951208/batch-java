package com.example.batch.job.reader.nooffset.repository.util;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class NumberNoOffsetRepositoryUtils {
    public static Long getInitValue(JPAQuery<?> query, NumberPath<Long> numberPath, Order direction) {
        JPAQuery<?> clone = query.clone();
        if (Order.ASC.equals(direction)) return clone.select(numberPath.min()).fetchOne();
        else return clone.select(numberPath.max()).fetchOne();
    }

    public static Long getLastValue(JPAQuery<?> query, boolean isRestart) {
        JPAQuery<?> clone = query.clone();
        List<?> fetch = clone.fetch();
        if (CollectionUtils.isEmpty(fetch)) {
            return null;
        } else {
            if (isRestart) return (Long) fetch.get(0);
            else return (Long) fetch.get(fetch.size() - 1);
        }
    }

    public static BooleanBuilder createWhere(NumberPath<Long> numberPath, BooleanExpression eq, Order direction, Long lastValue) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(eq);

        if (lastValue == null) return builder;

        if (Order.ASC.equals(direction)) return builder.and(numberPath.gt(lastValue));
        else return builder.and(numberPath.lt(lastValue));
    }
}
