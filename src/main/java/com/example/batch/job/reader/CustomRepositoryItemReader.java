package com.example.batch.job.reader;

import com.example.batch.job.reader.util.RepositoryItemReaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CustomRepositoryItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements InitializingBean {
    protected Log logger = LogFactory.getLog(getClass());

    protected PagingAndSortingRepository<?, ?> repository;

    protected Sort sort;

    protected volatile int page = 0;

    protected int pageSize = 10;

    protected volatile int current = 0;

    protected List<?> arguments;

    protected volatile List<T> results;

    protected final Object lock = new Object();

    protected String methodName;

    public CustomRepositoryItemReader() {
        super();
        setName(ClassUtils.getShortName(CustomRepositoryItemReader.class));
    }

    public void setArguments(List<?> arguments) {
        this.arguments = arguments;
    }

    public void setSort(Map<String, Sort.Direction> sorts) {
        this.sort = convertToSort(sorts);
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setRepository(PagingAndSortingRepository<?, ?> repository) {
        this.repository = repository;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(repository != null, "A PagingAndSortingRepository is required");
        Assert.state(pageSize > 0, "Page size must be greater than 0");
        Assert.state(sort != null, "A sort is required");
        Assert.state(this.methodName != null && !this.methodName.isEmpty(), "methodName is required.");
        if (isSaveState()) {
            Assert.state(StringUtils.hasText(getName()), "A name is required when saveState is set to true.");
        }
    }

    @Nullable
    @Override
    protected T doRead() throws Exception {

        synchronized (lock) {
            boolean nextPageNeeded = (results != null && current >= results.size());

            if (results == null || nextPageNeeded) {

                if (logger.isDebugEnabled()) {
                    logger.debug("Reading page " + page);
                }

                results = doPageRead();
                page++;

                if (results.isEmpty()) {
                    return null;
                }

                if (nextPageNeeded) {
                    current = 0;
                }
            }

            if (current < results.size()) {
                T curLine = results.get(current);
                current++;
                return curLine;
            }
            else {
                return null;
            }
        }
    }

    @Override
    protected void jumpToItem(int itemLastIndex) throws Exception {
        synchronized (lock) {
            page = itemLastIndex / pageSize;
            current = itemLastIndex % pageSize;
        }
    }

    protected List<T> doPageRead() throws Exception {
        Pageable pageRequest = PageRequest.of(page, pageSize, sort);

        MethodInvoker invoker = RepositoryItemReaderUtils.createMethodInvoker(repository, methodName);

        List<Object> parameters = new ArrayList<>();

        if (arguments != null && arguments.size() > 0) {
            parameters.addAll(arguments);
        }

        parameters.add(pageRequest);

        invoker.setArguments(parameters.toArray());

        Slice<T> curPage = (Slice<T>) RepositoryItemReaderUtils.doInvoke(invoker);

        return curPage.getContent();
    }

    @Override
    protected void doOpen() throws Exception {
    }

    @Override
    protected void doClose() throws Exception {
        synchronized (lock) {
            current = 0;
            page = 0;
            results = null;
        }
    }

    private Sort convertToSort(Map<String, Sort.Direction> sorts) {
        List<Sort.Order> sortValues = new ArrayList<>();

        for (Map.Entry<String, Sort.Direction> curSort : sorts.entrySet()) {
            sortValues.add(new Sort.Order(curSort.getValue(), curSort.getKey()));
        }

        return Sort.by(sortValues);
    }
}
