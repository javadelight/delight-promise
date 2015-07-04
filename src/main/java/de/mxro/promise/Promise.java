package de.mxro.promise;

import de.mxro.fn.Closure;
import delight.async.Operation;

/**
 * <p>
 * Type for variables which hold promises.
 * 
 * @author <a href="http://www.mxro.de">Max Rohde</a>
 *
 * @param <ResultType>
 */
public interface Promise<ResultType> extends Operation<ResultType> {

    /**
     * <p>
     * Attempts to resolve this promise synchronously.
     * 
     * @return Result of the promise.
     */
    public ResultType get();

    /**
     * <p>
     * Register a listener of exceptions.
     * 
     * @param closure
     *            Closure to be called if an exception occurs in resolving the
     *            promise.
     */
    public void catchExceptions(Closure<Throwable> closure);

    /**
     * <p>
     * Registeres a listener for exceptions, which is triggered when no
     * listeners have been defined using {@link #catchExceptions(Closure)}
     * 
     * @param closure
     */
    public void addExceptionFallback(Closure<Throwable> closure);

    /**
     * <p>
     * Attempts to resolve this promise asynchronously.
     * 
     * @param closure
     */
    public void get(Closure<ResultType> closure);

    /**
     * <p>
     * If this promises has been resolved, the result obtained will be returned
     * by this function.
     * <p>
     * Otherwise, returns <code>null</code>.
     * 
     * @return
     */
    public ResultType cachedResult();

}
