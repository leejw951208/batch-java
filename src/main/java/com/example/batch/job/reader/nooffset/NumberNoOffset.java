package com.example.batch.job.reader.nooffset;

import com.example.batch.job.reader.nooffset.repository.NoOffsetRepository;
import com.example.batch.job.reader.util.RepositoryItemReaderUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.NumberPath;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MethodInvoker;

import java.util.LinkedList;
import java.util.List;


public class NumberNoOffset<T extends Number & Comparable<?>> extends NoOffset<T> {
    private final NumberPath<T> numberPath;
    private T lastValue;

    public NumberNoOffset(Path<?> path, NumberPath<T> numberPath, NoOffsetRepository noOffsetRepository) {
        super(path, noOffsetRepository);
        this.numberPath = numberPath;
    }

    @Override
    public T lastValue(Pageable pageable, List<?> arguments) throws Exception {
        MethodInvoker invoker = RepositoryItemReaderUtils.createMethodInvoker(noOffsetRepository, methodName);

        List<Object> parameters = new LinkedList<>();

        if (!CollectionUtils.isEmpty(arguments)) parameters.addAll(arguments);

        parameters.add(path);
        parameters.add(numberPath);
        parameters.add(lastValue);
        parameters.add(pageable);

        invoker.setArguments(parameters.toArray());

        lastValue = (T) RepositoryItemReaderUtils.doInvoke(invoker);

        return lastValue;
    }
}
