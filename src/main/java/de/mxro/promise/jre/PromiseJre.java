package de.mxro.promise.jre;

import java.util.ArrayList;
import java.util.List;

import de.mxro.async.jre.internal.JrePromiseImpl;
import de.mxro.async.promise.Deferred;
import de.mxro.async.promise.Promise;
import de.mxro.async.promise.PromiseFactory;

public class PromiseJre {

    public static <ResultType> Promise<ResultType> promise(final Deferred<ResultType> promise) {
        return new JrePromiseImpl<ResultType>(promise);
    }

    public static PromiseFactory promiseFactory() {
        return new PromiseFactory() {

            @Override
            public <T> Promise<T> promise(final Deferred<T> deferred) {
                return PromiseJre.promise(deferred);
            }
        };
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Object> parallel(final Deferred... promises) {
        final ArrayList<Promise> list = new ArrayList<Promise>(promises.length);
        for (final Deferred ap : promises) {
            list.add(promise(ap));
        }

        return parallel(list.toArray(new Promise[0]));
    }

}
