package de.mxro.promise.jre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.mxro.async.AsyncCommon;
import de.mxro.async.Operation;
import de.mxro.async.AsyncFunction;
import de.mxro.async.callbacks.ListCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.promise.helper.PromiseTemplate;
import de.mxro.promise.helper.PromiseFactory;
import de.mxro.promise.jre.internal.JrePromiseImpl;

public class Promise {

    public static <ResultType> PromiseTemplate<ResultType> create(final Operation<ResultType> operation) {
        return new JrePromiseImpl<ResultType>(operation);
    }

    public static <T> List<Object> parallel(final List<PromiseTemplate<T>> promises) {
        return parallel(promises.toArray(new PromiseTemplate[0]));
    }

    @SuppressWarnings("rawtypes")
    public static List<Object> parallel(final PromiseTemplate... promises) {

        final CountDownLatch latch = new CountDownLatch(1);

        AsyncCommon.map(Arrays.asList(promises), new AsyncFunction<PromiseTemplate, Object>() {

            @SuppressWarnings("unchecked")
            @Override
            public void apply(final PromiseTemplate input, final ValueCallback<Object> callback) {
                input.apply(new ValueCallback<Object>() {

                    @Override
                    public void onFailure(final Throwable t) {
                        callback.onFailure(t);
                    }

                    @Override
                    public void onSuccess(final Object value) {
                        callback.onSuccess(value);
                    }
                });
            }
        }, new ListCallback<Object>() {

            @Override
            public void onSuccess(final List<Object> value) {
                latch.countDown();
            }

            @Override
            public void onFailure(final Throwable t) {
                latch.countDown();
            }
        });

        try {
            latch.await(120000, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (latch.getCount() > 0) {
            throw new RuntimeException("Parallel operation was not completed in timeout.");
        }

        final List<Object> res = new ArrayList<Object>(promises.length);

        for (final PromiseTemplate p : promises) {
            res.add(p.get());
        }

        return res;

    }

    public static PromiseFactory factory() {
        return new PromiseFactory() {

            @Override
            public <T> PromiseTemplate<T> promise(final Operation<T> deferred) {
                return Promise.create(deferred);
            }
        };
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Object> parallel(final Operation... promises) {
        final ArrayList<PromiseTemplate> list = new ArrayList<PromiseTemplate>(promises.length);
        for (final Operation ap : promises) {
            list.add(create(ap));
        }

        return parallel(list.toArray(new PromiseTemplate[0]));
    }

}
