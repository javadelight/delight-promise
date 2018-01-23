package delight.promise.jre.internal;

import delight.async.Operation;
import delight.async.callbacks.ValueCallback;
import delight.async.jre.Async;
import delight.promise.internal.PromiseImpl;

public class JrePromiseImpl<ResultType> extends PromiseImpl<ResultType> {

    private final class WrappedOperation implements Operation<ResultType> {
		@Override
		public void apply(final ValueCallback<ResultType> callback) {
		    JrePromiseImpl.this.apply(callback);
		}

		@Override
		public String toString() {
			return "WrappedOperation ["+JrePromiseImpl.this.operation+"]";
		}
		
		
	}

	private final boolean ENABLE_LOG = false;

    @Override
    public ResultType get() {

        if (this.failureCache.get() != null) {
            throw new RuntimeException("Promise failed before.", this.failureCache.get());
        }

        final ResultType cachedResult = cachedResult();

        if (cachedResult != null) {

            return cachedResult;
        }

        if (ENABLE_LOG) {
            System.out.println(this + ": Request result.");
        }

        Async.waitFor(120000, new WrappedOperation());

        if (ENABLE_LOG) {
            System.out.println(this + ": Obtained result.");
        }

        if (this.failureCache.get() != null) {
            throw new RuntimeException("Promise could not be resolved.", this.failureCache.get());
        }

        return cachedResult();
    }

    public JrePromiseImpl(final Operation<ResultType> operation) {
        super(operation);
    }

}
