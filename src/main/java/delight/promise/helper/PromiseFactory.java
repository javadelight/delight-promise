package delight.promise.helper;

import delight.async.Operation;
import delight.promise.Promise;

public interface PromiseFactory {

    public <T> Promise<T> promise(Operation<T> deferred);

}
