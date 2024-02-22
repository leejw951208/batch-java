package com.example.batch.job.reader.util;

import org.springframework.batch.item.adapter.AbstractMethodInvokingDelegator;
import org.springframework.batch.item.adapter.DynamicMethodInvocationException;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

public class RepositoryItemReaderUtils {
    public static Object doInvoke(MethodInvoker invoker) throws Exception {
        try {
            invoker.prepare();
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new DynamicMethodInvocationException(e);
        }

        try {
            return invoker.invoke();
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
            else {
                throw new AbstractMethodInvokingDelegator.InvocationTargetThrowableWrapper(e.getCause());
            }
        }
        catch (IllegalAccessException e) {
            throw new DynamicMethodInvocationException(e);
        }
    }

    public static MethodInvoker createMethodInvoker(Object targetObject, String targetMethod) {
        MethodInvoker invoker = new MethodInvoker();
        invoker.setTargetObject(targetObject);
        invoker.setTargetMethod(targetMethod);
        return invoker;
    }
}
