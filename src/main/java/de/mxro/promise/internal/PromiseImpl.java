package de.mxro.promise.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.mxro.async.Operation;
import de.mxro.async.Value;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.fn.Closure;
import de.mxro.promise.Promise;

public class PromiseImpl<ResultType> implements Promise<ResultType> {

    private final Operation<ResultType> asyncPromise;

    private final List<ValueCallback<ResultType>> deferredCalls;

    protected final Value<ResultType> resultCache;
    private final Value<Boolean> isRequesting;
    protected final Value<Throwable> failureCache;
    private final List<Closure<Throwable>> exceptionCatchers;

    @Override
    public void apply(final ValueCallback<ResultType> callback) {
        requestResult(callback);
    }

    private final void requestResult(final ValueCallback<ResultType> callback) {

        final boolean triggerOnFailure;
        final boolean triggerOnSuccess;
        synchronized (failureCache) {
            triggerOnFailure = failureCache.get() != null;

            if (!triggerOnFailure) {

                synchronized (resultCache) {
                    triggerOnSuccess = resultCache.get() != null;

                    if (!triggerOnSuccess) {
                        synchronized (isRequesting) {

                            if (isRequesting.get()) {
                                synchronized (deferredCalls) {
                                    deferredCalls.add(callback);
                                }
                                return;
                            } else {
                                isRequesting.set(true);
                            }

                        }
                    }

                }
            } else {
                triggerOnSuccess = false;
            }
        }

        if (triggerOnFailure) {
            callback.onFailure(failureCache.get());
            return;
        }

        if (triggerOnSuccess) {
            callback.onSuccess(resultCache.get());
            return;
        }

        asyncPromise.apply(new ValueCallback<ResultType>() {

            @Override
            public void onFailure(final Throwable t) {
                final List<ValueCallback<ResultType>> cachedCalls;
                synchronized (failureCache) {
                    failureCache.set(t);

                    synchronized (deferredCalls) {
                        cachedCalls = new ArrayList<ValueCallback<ResultType>>(deferredCalls);
                    }
                    deferredCalls.clear();
                }

                for (final ValueCallback<ResultType> deferredCb : cachedCalls) {
                    deferredCb.onFailure(t);
                }

                callback.onFailure(t);
            }

            @Override
            public void onSuccess(final ResultType value) {
                final List<ValueCallback<ResultType>> cachedCalls;
                synchronized (failureCache) {

                    // assert failureCache.get() == null :
                    // "Cached exception already set for operation [" +
                    // asyncPromise
                    // + "]. Failure: " + failureCache.get();
                    synchronized (resultCache) {
                        resultCache.set(value);

                        synchronized (deferredCalls) {
                            cachedCalls = new ArrayList<ValueCallback<ResultType>>(deferredCalls);
                        }
                        deferredCalls.clear();
                    }

                }

                for (final ValueCallback<ResultType> deferredCb : cachedCalls) {
                    deferredCb.onSuccess(value);
                }

                callback.onSuccess(value);
            }
        });

    }

    @Override
    public ResultType get() {

        get(new Closure<ResultType>() {

            @Override
            public void apply(final ResultType o) {

            }
        });

        synchronized (this.failureCache) {
            if (this.failureCache.get() != null) {
                throw new RuntimeException(this.failureCache.get());
            }
        }

        ResultType resultCache2;
        synchronized (this.resultCache) {
            resultCache2 = this.resultCache.get();

        }
        return resultCache2;
    }

    @Override
    public void catchExceptions(final Closure<Throwable> closure) {
        assert this.resultCache.get() == null && this.failureCache.get() == null;

        synchronized (exceptionCatchers) {
            exceptionCatchers.add(closure);
        }
    }

    @Override
    public void get(final Closure<ResultType> closure) {

        requestResult(new ValueCallback<ResultType>() {

            @Override
            public void onFailure(final Throwable t) {
                final ArrayList<Closure<Throwable>> catchers;
                synchronized (exceptionCatchers) {
                    catchers = new ArrayList<Closure<Throwable>>(exceptionCatchers);
                }
                for (final Closure<Throwable> exceptionCatcher : catchers) {
                    exceptionCatcher.apply(t);
                }
            }

            @Override
            public void onSuccess(final ResultType value) {
                closure.apply(value);
            }
        });
    }

    public PromiseImpl(final Operation<ResultType> asyncPromise) {
        super();
        this.asyncPromise = asyncPromise;
        this.deferredCalls = new LinkedList<ValueCallback<ResultType>>();
        this.resultCache = new Value<ResultType>(null);
        this.failureCache = new Value<Throwable>(null);
        this.exceptionCatchers = new LinkedList<Closure<Throwable>>();
        this.isRequesting = new Value<Boolean>(false);

    }

}
