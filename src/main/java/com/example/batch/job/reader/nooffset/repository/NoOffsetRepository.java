package com.example.batch.job.reader.nooffset.repository;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface NoOffsetRepository {

    Long findLastValue(BooleanExpression expression, Path<?> path, NumberPath<Long> numberPath, Long lastValue, Pageable pageable);
}
