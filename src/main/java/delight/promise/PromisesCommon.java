package delight.promise;

import delight.async.AsyncCommon;
import delight.async.AsyncFunction;
import delight.async.Operation;
import delight.async.callbacks.ListCallback;
import delight.async.callbacks.ValueCallback;
import delight.factories.Configuration;
import delight.factories.Dependencies;
import delight.factories.Factory;
import delight.promise.helper.PromiseFactory;
import delight.promise.internal.PromiseImpl;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Promise operations which are portable across Java and JavaScript.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 */
public class PromisesCommon {

    /**
     * <p>
     * A basic promise implementation which does not allow synchronous access
     * via .get().
     * 
     * @param operation
     *            The operation from which this promises should be built.
     * @return A new promise wrapping the supplied operation.
     */
    public final static <ResultType> Promise<ResultType> createUnsafe(final Operation<ResultType> operation) {
        return new PromiseImpl<ResultType>(operation);
    }

    /**
     * <p>
     * Creates a factory for unsafe promises.
     * 
     * @return A factory for unsafe promises.
     */
    private static PromiseFactory createDirectUnsafePromiseFactory() {
        return new PromiseFactory() {

            @Override
            public <T> Promise<T> promise(final Operation<T> deferred) {
                return PromisesCommon.createUnsafe(deferred);
            }
        };
    }

    /**
     * <p>
     * Creates a factory for unsafe promises.
     * 
     * @return A factory for unsafe promises.
     */
    public static Factory<?, ?, ?> createUnsafePromiseFactory() {
        return new Factory<PromiseFactory, PromiseConfiguration, Dependencies>() {

            @Override
            public boolean canInstantiate(final Configuration conf) {

                return conf instanceof PromiseConfiguration;
            }

            @Override
            public PromiseFactory create(final PromiseConfiguration conf, final Dependencies dependencies) {

                return PromisesCommon.createDirectUnsafePromiseFactory();
            }

        };
    }

    @SuppressWarnings("rawtypes")
    public static void resolve(final ValueCallback<List<Object>> callback, final Promise... promises) {
        AsyncCommon.map(Arrays.asList(promises), new AsyncFunction<Promise, Object>() {
    
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
                callback.onSuccess(value);
            }
    
            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }
        });
    }

}
