package com.example.batch.job.reader.zerooffset;

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

public class ZeroOffsetRepositoryItemReader <T> extends CustomRepositoryItemReader<T> {
    public ZeroOffsetRepositoryItemReader() {
        super();
        setName(ClassUtils.getShortName(ZeroOffsetRepositoryItemReader.class));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<T> doPageRead() throws Exception {
        Pageable pageRequest = PageRequest.of(0, pageSize, sort);

        MethodInvoker invoker = RepositoryItemReaderUtils.createMethodInvoker(repository, methodName);

        List<Object> parameters = new LinkedList<>();

        if (!CollectionUtils.isEmpty(arguments)) parameters.addAll(arguments);

        parameters.add(pageRequest);

        invoker.setArguments(parameters.toArray());

        Slice<T> curPage = (Slice<T>) RepositoryItemReaderUtils.doInvoke(invoker);

        return curPage.getContent();
    }
}
