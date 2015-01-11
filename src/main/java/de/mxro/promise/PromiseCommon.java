package de.mxro.promise;

import de.mxro.async.Operation;
import de.mxro.promise.helper.P;
import de.mxro.promise.helper.PromiseFactory;
import de.mxro.promise.internal.PromiseImpl;

public class PromiseCommon {

    public final static <ResultType> P<ResultType> promise(final Operation<ResultType> promise) {
        return new PromiseImpl<ResultType>(promise);
    }

    public static PromiseFactory promiseFactory() {
        return new PromiseFactory() {

            @Override
            public <T> P<T> promise(final Operation<T> deferred) {
                return PromiseCommon.promise(deferred);
            }
        };
    }

}
