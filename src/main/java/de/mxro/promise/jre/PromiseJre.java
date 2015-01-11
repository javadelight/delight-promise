package de.mxro.promise.jre;

import de.mxro.async.jre.internal.JrePromiseImpl;
import de.mxro.async.promise.Deferred;
import de.mxro.async.promise.Promise;

public class PromiseJre {

    public static <ResultType> Promise<ResultType> promise(final Deferred<ResultType> promise) {
        return new JrePromiseImpl<ResultType>(promise);
    }

}
