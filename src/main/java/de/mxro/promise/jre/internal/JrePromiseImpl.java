package de.mxro.promise.jre.internal;

import de.mxro.promise.internal.PromiseImpl;
import delight.async.Operation;
import delight.async.callbacks.ValueCallback;
import delight.async.jre.Async;

public class JrePromiseImpl<ResultType> extends PromiseImpl<ResultType> {

    @Override
    public ResultType get() {

        if (this.failureCache.get() != null) {
            throw new RuntimeException("Promise failed before.", this.failureCache.get());
        }

        final ResultType cachedResult = cachedResult();

        if (cachedResult != null) {

            return cachedResult;
        }

        Async.waitFor(new Operation<ResultType>() {

            @Override
            public void apply(final ValueCallback<ResultType> callback) {
                JrePromiseImpl.this.apply(callback);
            }
        });

        if (this.failureCache.get() != null) {
            throw new RuntimeException("Promise could not be resolved.", this.failureCache.get());
        }

        return cachedResult();
    }

    public JrePromiseImpl(final Operation<ResultType> operation) {
        super(operation);
    }

}
