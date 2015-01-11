package de.mxro.promise;

import de.mxro.async.promise.Deferred;
import de.mxro.async.promise.Promise;
import de.mxro.async.promise.PromiseFactory;

public class PromiseCommon {

    public final static <ResultType> Promise<ResultType> promise(final Deferred<ResultType> promise) {
        return new PromiseImpl<ResultType>(promise);
    }

    public static PromiseFactory promiseFactory() {
        return new PromiseFactory() {

            @Override
            public <T> Promise<T> promise(final Deferred<T> deferred) {
                return PromiseCommon.promise(deferred);
            }
        };
    }

}
