package delight.promise.jre;

import delight.async.AsyncCommon;
import delight.async.AsyncFunction;
import delight.async.Operation;
import delight.async.callbacks.ListCallback;
import delight.async.callbacks.ValueCallback;
import delight.async.jre.Async;
import delight.factories.Configuration;
import delight.factories.Dependencies;
import delight.factories.Factory;
import delight.promise.Promise;
import delight.promise.PromiseConfiguration;
import delight.promise.helper.PromiseFactory;
import delight.promise.jre.internal.JrePromiseImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create and resolve promises in a JSE/Android/OSGi environment.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 */
public class Promises {

    /**
     * Creates a new promise.
     * 
     * @param operation
     *            The operation executed when this promise is resolved.
     * @return A new promise instance wrapping the provided operation.
     */
    public static <ResultType> Promise<ResultType> create(final Operation<ResultType> operation) {
        return new JrePromiseImpl<ResultType>(operation);
    }

    /**
     * <p>
     * Creates a factory, which instantiates promieses directly instead of a
     * factory for {@link PromiseFactor} as {@link #createPromiseFactory()}
     * does.
     * 
     * @return
     */
    public static PromiseFactory createDirectFactory() {
        return new PromiseFactory() {

            @Override
            public <T> Promise<T> promise(final Operation<T> deferred) {
                return Promises.create(deferred);
            }
        };
    }

    /**
     * <p>
     * Creates a factory for promises.
     * 
     * @return A factory for promises.
     */
    public static Factory<?, ?, ?> createPromiseFactory() {
        return new Factory<PromiseFactory, PromiseConfiguration, Dependencies>() {

            @Override
            public boolean canInstantiate(final Configuration conf) {

                return conf instanceof PromiseConfiguration;
            }

            @Override
            public PromiseFactory create(final PromiseConfiguration conf, final Dependencies dependencies) {

                return Promises.createDirectFactory();
            }

        };
    }

    /**
     * Resolves the provided promises in parallel.
     * 
     * @param promises
     * @return
     */
    public static <T> List<Object> parallel(final List<Promise<T>> promises) {
        return parallel(promises.toArray(new Promise[0]));
    }

    /**
     * <p>
     * Resolves the provided promises in parallel.
     * <p>
     * Blocks the calling thread until all promises are resolved.
     * 
     * @param promises
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static List<Object> parallel(final Promise... promises) {

        return Async.waitFor(new Operation<List<Object>>() {

            @Override
            public void apply(final ValueCallback<List<Object>> callback) {
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

        });

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Object> parallel(final Operation... promises) {
        final ArrayList<Promise> list = new ArrayList<Promise>(promises.length);
        for (final Operation ap : promises) {
            list.add(create(ap));
        }

        return parallel(list.toArray(new Promise[0]));
    }

}
