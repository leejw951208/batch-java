package com.example.batch.job.reader.nooffset;

import com.example.batch.job.reader.CustomRepositoryItemReader;
import com.example.batch.job.reader.util.RepositoryItemReaderUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MethodInvoker;

import java.util.LinkedList;
import java.util.List;

/**
 * NoOffset
 *  - 기존 offset 방식의 페이징 처리는 데이터가 많아질수록 성능 저하가 심해진다.
 *  - 예를 들어 offset 10000, limit 10 이라고 하면 최종적으로 10010건의 데이터를 읽어야 한다.
 *  - NoOffset 방식은 정렬 기준이 되는 필드를 조건절에 추가하여 데이터를 조회하는 방식
 *  - order by id desc 라는 정렬 조건이 있다면 where member.id < id 조건을 추가하고 limit 10 으로 조회한다.
 *  - NoOffset을 적용하기 위해서는 정렬 조건이 PK이거나 인덱스 컬럼이어야 한다. 그리고 정렬 조건이 1개 여야 한다.
 */
public class NoOffsetRepositoryItemReader<T> extends CustomRepositoryItemReader<T> {
    private NoOffset<?> noOffset;

    public NoOffsetRepositoryItemReader() {
        super();
        setName(ClassUtils.getShortName(NoOffsetRepositoryItemReader.class));
    }

    public void setNoOffset(NoOffset<?> noOffset) {
        this.noOffset = noOffset;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<T> doPageRead() throws Exception {
        if (noOffset == null) throw new RuntimeException("Need to add NoOffset object");

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        MethodInvoker invoker = RepositoryItemReaderUtils.createMethodInvoker(repository, methodName);

        List<Object> parameters = new LinkedList<>();

        if (!CollectionUtils.isEmpty(arguments)) parameters.addAll(arguments);

        parameters.add(noOffset.lastValue(pageable, arguments));
        parameters.add(pageable);

        invoker.setArguments(parameters.toArray());

        Slice<T> curPage = (Slice<T>) RepositoryItemReaderUtils.doInvoke(invoker);

        return curPage.getContent();
    }
}
