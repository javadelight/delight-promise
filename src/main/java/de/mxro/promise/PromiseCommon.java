package de.mxro.promise;

import de.mxro.async.Operation;
import de.mxro.promise.helper.PromiseTemplate;
import de.mxro.promise.helper.PromiseFactory;
import de.mxro.promise.internal.PromiseImpl;

public class PromiseCommon {

    public final static <ResultType> PromiseTemplate<ResultType> promise(final Operation<ResultType> promise) {
        return new PromiseImpl<ResultType>(promise);
    }

    public static PromiseFactory promiseFactory() {
        return new PromiseFactory() {

            @Override
            public <T> PromiseTemplate<T> promise(final Operation<T> deferred) {
                return PromiseCommon.promise(deferred);
            }
        };
    }

}
