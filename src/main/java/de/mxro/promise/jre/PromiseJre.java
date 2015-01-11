package de.mxro.promise.jre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.mxro.async.Async;
import de.mxro.async.Deferred;
import de.mxro.async.Operation;
import de.mxro.async.callbacks.ListCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.promise.helper.Promise;
import de.mxro.promise.helper.PromiseFactory;
import de.mxro.promise.jre.internal.JrePromiseImpl;

public class PromiseJre {

    public static <ResultType> Promise<ResultType> create(final Deferred<ResultType> operation) {
        return new JrePromiseImpl<ResultType>(operation);
    }

    public static <T> List<Object> parallel(final List<Promise<T>> promises) {
        return parallel(promises.toArray(new Promise[0]));
    }

    @SuppressWarnings("rawtypes")
    public static List<Object> parallel(final Promise... promises) {

        final CountDownLatch latch = new CountDownLatch(1);

        Async.map(Arrays.asList(promises), new Operation<Promise, Object>() {

            @SuppressWarnings("unchecked")
            @Override
            public void apply(final Promise input, final ValueCallback<Object> callback) {
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

        for (final Promise p : promises) {
            res.add(p.get());
        }

        return res;

    }

    public static PromiseFactory promiseFactory() {
        return new PromiseFactory() {

            @Override
            public <T> Promise<T> promise(final Deferred<T> deferred) {
                return PromiseJre.create(deferred);
            }
        };
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Object> parallel(final Deferred... promises) {
        final ArrayList<Promise> list = new ArrayList<Promise>(promises.length);
        for (final Deferred ap : promises) {
            list.add(create(ap));
        }

        return parallel(list.toArray(new Promise[0]));
    }

}
