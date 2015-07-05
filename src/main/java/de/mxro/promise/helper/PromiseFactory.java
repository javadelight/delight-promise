package de.mxro.promise.helper;

import delight.async.Operation;

import de.mxro.promise.Promise;

public interface PromiseFactory {

    public <T> Promise<T> promise(Operation<T> deferred);

}
