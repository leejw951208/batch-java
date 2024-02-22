package com.example.batch.job.reader.nooffset;

import com.example.batch.job.reader.nooffset.repository.NoOffsetRepository;
import com.querydsl.core.types.Path;
import org.springframework.data.domain.Pageable;

import java.util.List;

public abstract class NoOffset<T extends Number & Comparable<?>> {
    protected final Path<?> path;
    protected final String methodName;
    protected final NoOffsetRepository noOffsetRepository;

    public NoOffset(Path<?> path, NoOffsetRepository noOffsetRepository) {
        this.path = path;
        this.noOffsetRepository = noOffsetRepository;
        this.methodName = "findLastValue";
    }

    public abstract T lastValue(Pageable pageable, List<?> arguments) throws Exception;
}
