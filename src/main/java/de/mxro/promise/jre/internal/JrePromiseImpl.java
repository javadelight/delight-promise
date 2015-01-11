package de.mxro.promise.jre.internal;

import de.mxro.async.Operation;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.jre.Async;
import de.mxro.promise.internal.PromiseImpl;

public class JrePromiseImpl<ResultType> extends PromiseImpl<ResultType> {

    @Override
    public ResultType get() {

        if (this.failureCache.get() != null) {
            throw new RuntimeException("Promise failed before.", this.failureCache.get());
        }

        if (this.resultCache.get() != null) {
            return this.resultCache.get();
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

        return this.resultCache.get();
    }

    public JrePromiseImpl(final Operation<ResultType> asyncPromise) {
        super(asyncPromise);
    }

}
