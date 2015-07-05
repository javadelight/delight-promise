package de.mxro.promise.internal;

import delight.async.Operation;
import delight.async.Value;
import delight.async.callbacks.ValueCallback;
import delight.functional.Closure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.mxro.promise.Promise;

public class PromiseImpl<ResultType> implements Promise<ResultType> {

    private final static boolean ENABLE_LOG = false;

    private final Operation<ResultType> operation;

    private final List<ValueCallback<ResultType>> deferredCalls;

    private final Value<ResultType> resultCache;
    private final Value<Boolean> isRequesting;
    protected final Value<Throwable> failureCache;
    private final List<Closure<Throwable>> exceptionCatchers;
    private final List<Closure<Throwable>> exceptionFallbackCatchers;

    @Override
    public void apply(final ValueCallback<ResultType> callback) {
        requestResult(callback);
    }

    @Override
    public ResultType cachedResult() {
        if (ENABLE_LOG) {
            System.out.println(this + ": Retrieving result " + resultCache.get());
        }
        return resultCache.get();
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

        operation.apply(new ValueCallback<ResultType>() {

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

                        if (ENABLE_LOG) {
                            System.out.println(PromiseImpl.this + ": Set result " + resultCache);
                        }

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
                if (catchers.size() == 0) {
                    final ArrayList<Closure<Throwable>> fallbackCatchers;
                    synchronized (exceptionFallbackCatchers) {
                        fallbackCatchers = new ArrayList<Closure<Throwable>>(exceptionFallbackCatchers);
                    }

                    for (final Closure<Throwable> exceptionCatcher : fallbackCatchers) {
                        exceptionCatcher.apply(t);
                    }

                    if (fallbackCatchers.size() == 0) {
                        throw new RuntimeException(
                                "No catchException or addExceptionFallback defined for promise over [" + operation
                                + "].", t);
                    }
                }
            }

            @Override
            public void onSuccess(final ResultType value) {
                closure.apply(value);
            }
        });
    }

    public PromiseImpl(final Operation<ResultType> operation) {
        super();
        this.operation = operation;
        this.deferredCalls = new LinkedList<ValueCallback<ResultType>>();
        this.resultCache = new Value<ResultType>(null);
        this.failureCache = new Value<Throwable>(null);
        this.exceptionCatchers = new LinkedList<Closure<Throwable>>();
        this.exceptionFallbackCatchers = new LinkedList<Closure<Throwable>>();
        this.isRequesting = new Value<Boolean>(false);

    }

    @Override
    public String toString() {

        return "[(" + operation + ") wrapped by (" + super.toString() + ")]";
    }

    @Override
    public void addExceptionFallback(final Closure<Throwable> closure) {
        this.exceptionFallbackCatchers.add(closure);
    }

}
