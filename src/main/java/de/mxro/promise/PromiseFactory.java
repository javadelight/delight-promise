package de.mxro.promise;

import de.mxro.async.Deferred;

public interface PromiseFactory {

    public <T> Promise<T> promise(Deferred<T> deferred);

}
