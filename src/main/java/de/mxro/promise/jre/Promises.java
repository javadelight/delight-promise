package de.mxro.promise.jre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.mxro.async.AsyncCommon;
import de.mxro.async.AsyncFunction;
import de.mxro.async.Operation;
import de.mxro.async.callbacks.ListCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.async.jre.Async;
import de.mxro.factories.Configuration;
import de.mxro.factories.Dependencies;
import de.mxro.factories.Factory;
import de.mxro.promise.Promise;
import de.mxro.promise.PromiseConfiguration;
import de.mxro.promise.helper.PromiseFactory;
import de.mxro.promise.jre.internal.JrePromiseImpl;

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

    public static PromiseFactory factory() {
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
    public static Factory<?, ?, ?> createUnsafePromiseFactory() {
        return new Factory<PromiseFactory, PromiseConfiguration, Dependencies>() {

            @Override
            public boolean canInstantiate(final Configuration conf) {

                return conf instanceof PromiseConfiguration;
            }

            @Override
            public PromiseFactory create(final PromiseConfiguration conf, final Dependencies dependencies) {

                return Promises.factory();
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
