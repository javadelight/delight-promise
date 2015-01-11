package de.mxro.promise.helper;

import de.mxro.async.Operation;

public interface PromiseFactory {

    public <T> PromiseTemplate<T> promise(Operation<T> deferred);

}
