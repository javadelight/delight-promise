package de.mxro.promise;

import de.mxro.async.Operation;
import de.mxro.promise.helper.PromiseFactory;
import de.mxro.promise.internal.PromiseImpl;

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
    public static PromiseFactory unsafePromiseFactory() {
        return new PromiseFactory() {

            @Override
            public <T> Promise<T> promise(final Operation<T> deferred) {
                return PromisesCommon.createUnsafe(deferred);
            }
        };
    }

}
