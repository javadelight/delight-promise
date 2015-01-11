package de.mxro.promise.helper;

import de.mxro.async.Deferred;

public interface PromiseFactory {

    public <T> Promise<T> promise(Deferred<T> deferred);

}
