package de.mxro.promise.jre.internal;

import de.mxro.async.Operation;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.jre.Async;
import de.mxro.promise.internal.PromiseImpl;

public class JrePromiseImpl<ResultType> extends PromiseImpl<ResultType> {

    @Override
    public ResultType get() {

        final ResultType result = super.get();

        if (result != null) {
            return result;
        }

        Async.waitFor(new Operation<ResultType>() {

            @Override
            public void apply(final ValueCallback<ResultType> callback) {
                JrePromiseImpl.this.apply(callback);
            }
        });

        return get();

        /*
         * final CountDownLatch latch = new CountDownLatch(1);
         * 
         * 
         * 
         * apply(new ValueCallback<ResultType>() {
         * 
         * @Override public void onFailure(Throwable t) { latch.countDown(); }
         * 
         * @Override public void onSuccess(ResultType value) {
         * latch.countDown(); } });
         * 
         * try {
         * 
         * latch.await(320000, TimeUnit.MILLISECONDS); } catch (final
         * InterruptedException e) { throw new RuntimeException(e); }
         * 
         * if (latch.getCount() > 0) { throw new RuntimeException(
         * "Get call could not be completed in 320 s timeout."); }
         * 
         * return get();
         */

    }

    public JrePromiseImpl(final Operation<ResultType> asyncPromise) {
        super(asyncPromise);
    }

}
